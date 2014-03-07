package com.funshion.search.utils.systemWatcher;

import java.util.ArrayList;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.MessageService.Client;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;

public class MessageClient {
	public static QueryMessage emptyQuery(String messageName){
		QueryMessage qm = new QueryMessage();
		qm.messageName = messageName;
		qm.ienv = "SSD@" + System.getProperty(SSDaemonService.envPropName) ;
		qm.messageBody = new ArrayList<String>();
		return qm;
	}
	public final String ip;
	public final int port;
	public final Client client;
	public TTransport transport;
	
	public MessageClient(String ip, int port) throws TTransportException{
		this(ip, port, 1000);
	}
	public MessageClient(String ip, int port, int timeout) throws TTransportException{
		this.ip = ip;
		this.port = port;
		transport = new TSocket(ip, port, timeout); 
		transport.open(); 
		TProtocol protocol = new TBinaryProtocol(transport); 
		client = new Client(protocol); 
	}
	
	public MessageClient(int port) throws TTransportException {
		this("127.0.0.1", port);
	}

	public void close(){
		if(transport != null){
			transport.close(); 
		}
	}
	public void finlize(){
		this.close();
	}

	public AnswerMessage queryMsg(String[]args) throws TException{
		if(args.length == 0){
			throw new TException("cmd name not set! use help");
		}
		QueryMessage qm = emptyQuery(args[0]);
		for(int x = 1; x < args.length; x ++){
			qm.addToMessageBody(args[x]);
		}
		return queryMsg(qm);
	}
	public AnswerMessage queryMsg(QueryMessage qm) throws TException {
		return client.queryMsg(qm);
	}
}