package com.funshion.videoService.messageQueue.test;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.funshion.videoService.messageQueue.HostInfo;
import com.funshion.videoService.messageQueue.RabbitMqReader;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class ReceiveLogsDirect {
	
	public static void main(String[] argv) throws Exception {
		argv = new String[]{"ROUTING_VIDEOID", "warning", "error"};
		HostInfo[]hosts = new HostInfo[]{new HostInfo("192.168.16.51", ConnectionFactory.DEFAULT_AMQP_PORT),
				new HostInfo("192.168.16.21", ConnectionFactory.DEFAULT_AMQP_PORT)};
		ConcurrentLinkedQueue<QueueingConsumer.Delivery> queue = 
				new ConcurrentLinkedQueue<QueueingConsumer.Delivery>();
		RabbitMqReader reader = new RabbitMqReader(EmitLogDirect.EXCHANGE_NAME, hosts, queue, argv);
		reader.start();
		while (true) {
			try{
				QueueingConsumer.Delivery delivery = queue.poll();
				if(delivery == null){
					continue;
				}
				String message = "" + new String(delivery.getBody());
				String routingKey = delivery.getEnvelope().getRoutingKey();

				System.out.println(" [x] Received '" + routingKey + "':'" + message + "'");
			}finally{
				Thread.sleep(500);
			}
		}
	}
}