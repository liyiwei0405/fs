package com.funshion.search.utils;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;

public abstract class TCPServer extends Thread{
	private final int port;
	private  boolean stop = false;
	protected final ServerSocket listenSocket;
	protected ExecutorService  exec;
	public TCPServer(int port) throws IOException{
		this.port = port;
		listenSocket = new ServerSocket();
		listenSocket.setReuseAddress(true);
		listenSocket.bind(new InetSocketAddress((InetAddress)null, port), 1000);
	}
	public abstract void doYourJob(Socket clientSocket);
	protected final void tryStop() {
		if(!this.stop) {
			this.stop = true;
			if(this.listenSocket != null) {
				try {
					this.listenSocket.close();
				} catch (IOException e) {
					LogHelper.log.error(e, "when stop tcp server, port = %d",
							this.port);
				}finally {

				}
			}
		}
	}
	/**
	 * start service
	 * here,listenSocket listen one port,when connect query is accepted,
	 * TC let  Hiconnection to manipulate the socket,and then keep listening 
	 */
	public void run() {
		try{
			listenSocket.setReuseAddress(true);
			do{
				try{
					Socket linked = listenSocket.accept();
					doYourJob(linked);
				}catch(SocketTimeoutException out){
					LogHelper.log.debug("Listen socket timeout, e=" + out);
				} catch(Exception e){
					LogHelper.log.error(e, "tcpServer got excetion");
				}

			}while(!stop);

		}catch(IOException e) {
			LogHelper.log.error(e, "Listen socket fail!");
		}
		this.stop=true;
	}
	
	public int getPort() {
		return this.port;
	}
	public void setSoTimeout(int i) throws SocketException {
		this.listenSocket.setSoTimeout(i);
	}
}


