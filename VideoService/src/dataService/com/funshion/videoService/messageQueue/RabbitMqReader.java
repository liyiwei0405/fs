package com.funshion.videoService.messageQueue;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.Connection;

public class RabbitMqReader extends Thread{
	static final LogHelper log = new LogHelper("mqReader");
	private final String exchangeName;
	private HostInfo[]hosts;
	final ConcurrentLinkedQueue<QueueingConsumer.Delivery> queue;
	final String[]routingKeys;
	final String depictName;
	public RabbitMqReader(ConfigReader cr, ConcurrentLinkedQueue<QueueingConsumer.Delivery> queue,
			String[]routingKeys){
		this(cr.getValue("exchangeName"), hostInfo(cr.getValue("hosts")), queue, routingKeys);
	}
	private static HostInfo[] hostInfo(String value) {
		String[] hosts = value.split(",");
		HostInfo[]infos = new HostInfo[hosts.length];
		for(int x = 0; x < hosts.length; x ++){
			String str = hosts[x].trim();
			String[]inf = str.split("\\:");
			
			infos[x] = new HostInfo(inf[0], Integer.parseInt(inf[1]));
		}
		return infos;
	}
	public RabbitMqReader(String exchangeName, HostInfo[]hosts, ConcurrentLinkedQueue<QueueingConsumer.Delivery> queue,
			String[]routingKeys){
		this.exchangeName = exchangeName;
		this.hosts = hosts;
		this.queue = queue;
		this.routingKeys = routingKeys;
		depictName = "mqReader: exchangeName = "+ this.exchangeName + ", host = " + Arrays.asList(hosts) + ", routingKeys = " + Arrays.asList(routingKeys) ;
	}

	public String toString(){
		return depictName; 
	}
	public void run(){
		while(true){
			try {
				readMessage();
			} catch (Throwable e) {
				log.error(e, "when read message by %s", this);
				e.printStackTrace();
			}finally{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					log.error(e, "when read message by %s", this);
					e.printStackTrace();
				}
			}
		}
	}

	private void readMessage() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException{

		ConnectionFactory factory = new ConnectionFactory();
		Connection connection = null;
		Channel channel = null;
		try{
			connection = factory.newConnection(null, HostInfo.list2Address(hosts));
			log.info("new connection got %s for %s", connection, this);
			channel = connection.createChannel();
			log.info("new channel got %s for %s", channel, this);
//			channel.exchangeDeclare(this.exchangeName, "direct");
			String queueName = channel.queueDeclare().getQueue();
			for(String routingKey : routingKeys){
				channel.queueBind(queueName, this.exchangeName, routingKey.trim());
			}

			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(queueName, true, consumer);
			log.info("start read dilivery for %s", this);
			while (true) {
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				log.debug("get new message with routingKey '%s'", delivery.getEnvelope().getRoutingKey());
				queue.add(delivery);
			}
		}finally{
			try{
				if(channel != null && channel.isOpen()){
					channel.close();
				}
				if(connection != null && connection.isOpen()){
					connection.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}

