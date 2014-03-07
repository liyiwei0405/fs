package com.funshion.videoService;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.funshion.search.utils.ConfigReader;
import com.funshion.videoService.messageQueue.HostInfo;
import com.funshion.videoService.messageQueue.RabbitMqReader;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public abstract class MessageReader implements Runnable{
	ConcurrentLinkedQueue<QueueingConsumer.Delivery> queue;
	public MessageReader(ConfigReader cr){
		String []routingKeys = cr.getValue("routingKeys").split(",");
		trimStrings(routingKeys);
		queue = new ConcurrentLinkedQueue<QueueingConsumer.Delivery>();

		String[]hosts = cr.getValue("rabbitServer").split(",");
		trimStrings(hosts);
		HostInfo[]hostsv = new HostInfo[hosts.length];
		for(int index = 0; index < hostsv.length; index ++){
			String x = hosts[index];
			String vv[] = x.split("\\:");
			String host = vv[0].trim();
			int port = Integer.parseInt(vv[1].trim());
			HostInfo hi = new HostInfo(host, port);
			hostsv[index] = hi;
		}
		String exchangeName = cr.getValue("exchangeName");

		RabbitMqReader reader = new RabbitMqReader(exchangeName, hostsv, queue, routingKeys);
		reader.start();
	}

	protected void trimStrings(String[]routingKeys){
		for(int x = 0; x < routingKeys.length; x ++){
			routingKeys[x] = routingKeys[x].trim();
		}
	}
	
	public void run(){
		while(true){
			Delivery msg = queue.poll();
			if(msg == null){
				break;
			}
			doInChannel(msg);
		}
	}

	protected abstract void doInChannel(Delivery msg);
}
