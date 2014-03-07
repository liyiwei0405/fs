package com.funshion.videoService.messageQueue.test;
import com.funshion.search.utils.Consoler;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class EmitLogDirect {

	static final String EXCHANGE_NAME = "EXCHANGE_VIDEO";

	public static void main(String[] argv) throws Exception {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.16.21");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

//		channel.exchangeDeclare(EXCHANGE_NAME, "direct");

		String severity = "error";
		String message = severity + " " + System.currentTimeMillis() ;
		while(true){
			String str = Consoler.readString("log>");
			if("quit".equalsIgnoreCase(str)){
				break;
			}
			String body[] = str.split("\\:");
			if(body.length != 2){
				severity = "ROUTING_VIDEOID";
				message = str;
			}else{
				severity = body[0];
				message = body[1];
			}
//			channel.basicPublish(EXCHANGE_NAME, severity, null, new int32_t(90154).toBytes());
			channel.basicPublish(EXCHANGE_NAME, severity, null, message.getBytes());
			System.out.println(" [x] Sent '" + severity + "':'" + message + "'");
			
		}
		channel.close();
		connection.close();
	}



}