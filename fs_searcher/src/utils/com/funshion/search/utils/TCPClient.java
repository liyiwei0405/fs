package com.funshion.search.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * this is a TCP client 
 * @author liying1
 *
 */

public class TCPClient {
	protected Socket skt;
	InputStream ips;
	OutputStream ops;
	private InetSocketAddress sa;
	int connectTimeout=10000;
	String remoteIp;
	public TCPClient(String ip,int port) throws IOException{
		this(ip,port,10000);
	}
	public TCPClient(char[] ip, int port) throws IOException {
		this(new String(ip).trim(),port);
	}

	public TCPClient(String ip, int port, int connectTimeout) throws IOException {
		this(ip,port,connectTimeout,null);
	}
	public TCPClient(String ip, int port, int connectTimeout,
			InetSocketAddress local) throws IOException {
		this.remoteIp = ip;
		this.connectTimeout = connectTimeout;
		sa = new InetSocketAddress(InetAddress.getByName(ip), port);

		skt = new Socket();
		if(local != null){
			skt.bind(local);
		}
		skt.connect(sa, connectTimeout);
		this.ips = skt.getInputStream();
		this.ops = skt.getOutputStream();
	}
	public String getRemoteIp(){
		return this.remoteIp;
	}
	/**
	 * write <code>bs</code> to TCPSocket
	 * @param bs
	 * @throws IOException
	 */
	public void write(byte[]bs) throws IOException{
		OutputStream os = skt.getOutputStream();
		os.write(bs);	
		os.flush();
	}
	
	/**
	 * read data from InputStream to <code>bs</code>
	 * @param bs
	 * @return
	 * @throws IOException
	 */
	public void readFull(byte[]bs) throws IOException{
		InputStream ips = skt.getInputStream();
		IOUtils.mustFillBuffer(ips, bs);
	}

	public void close(){
		if(this.skt!=null){
			try {
				this.skt.close();
			} catch (IOException e) {
				LogHelper.log.error(e, "when close socket %s", skt);
			}
			this.skt = null;
		}
	}
	public int read() throws IOException {
		return this.ips.read();
	}
	/**
	 * get the socket wrapped by this client
	 * @return
	 */
	public Socket getSocket(){
		return this.skt;
	}
	
	public void finalize() {
		this.close();
	}
	public InetSocketAddress getSa() {
		return sa;
	}
}
