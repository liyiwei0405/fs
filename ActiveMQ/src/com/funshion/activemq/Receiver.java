package com.funshion.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.funshion.search.utils.LogHelper;

public class Receiver {
	public static void main(String[] args) {
		final LogHelper log = new LogHelper("activeMQ");
		// ConnectionFactory ：连接工厂，JMS 用它创建连接
		ConnectionFactory connectionFactory;
		// Connection ：JMS 客户端到JMS Provider 的连接
		Connection connection = null;
		// Session： 一个发送或接收消息的线程
		Session session;

		//String host = args.length==0 ? "localhost" : args[0];
		// 消费者，消息接收者
		MessageConsumer consumer;
		connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD,
				"tcp://192.168.16.21:61616");
		try {
			// 构造从工厂得到连接对象
			connection = connectionFactory.createConnection();
			// 启动
			connection.start();
			// 获取操作连接
			session = connection.createSession(Boolean.FALSE,
					Session.AUTO_ACKNOWLEDGE);
			Topic topic = session.createTopic("phptojava"); 
			// 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
			consumer = session.createConsumer(topic);
			consumer.setMessageListener(new MessageListener(){

				@Override
				public void onMessage(Message message) {
					try{
						log.info("Received message");
					}catch(Exception e){
						e.printStackTrace();
					}
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}