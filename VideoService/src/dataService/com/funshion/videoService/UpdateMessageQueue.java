package com.funshion.videoService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.funshion.search.IndexableRecord;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
import com.funshion.videoService.messageQueue.RabbitMqReader;
import com.rabbitmq.client.QueueingConsumer;

public abstract class UpdateMessageQueue {
	LogHelper log = new LogHelper("videoIdMsgQ");
	final ConfigReader rabbitMqConfig;
	final ConcurrentLinkedQueue<QueueingConsumer.Delivery> queue;
	final RabbitMqReader reader;
	final ConfigReader cr;
	public UpdateMessageQueue(ConfigReader cr) throws IOException{
		this.cr = cr;
		rabbitMqConfig = new ConfigReader(cr.configFile,  cr.getValue("rabbitMqConfig", "rabbitMq"));
		String routingKeys[] = cr.getValue("routingKeys").split(",");
		queue = new ConcurrentLinkedQueue<QueueingConsumer.Delivery>();

		reader = new RabbitMqReader(rabbitMqConfig, queue,
				routingKeys);
	}

	public abstract void update(List<IndexableRecord>updates) throws Exception;
}
