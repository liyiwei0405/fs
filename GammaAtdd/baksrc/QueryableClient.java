package com.funshion.gamma.atdd;

import org.apache.thrift.protocol.TProtocol;

public abstract class QueryableClient {
	protected TProtocol protocol;
	public void setProtocol(TProtocol protocol){
		this.protocol = protocol;
	}
	public abstract Object queryAtdd(QueryEntry nextAtddQuery) throws Exception;
	public abstract Object queryServer(QueryEntry nextServerQuery) throws Exception;
}
