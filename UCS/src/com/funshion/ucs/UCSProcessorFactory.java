package com.funshion.ucs;

import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.transport.TTransport;

import com.funshion.ucs.thrift.UCS.Processor;


public class UCSProcessorFactory extends TProcessorFactory {
	public static final UCSProcessorFactory instance = new UCSProcessorFactory();
	
	private UCSProcessorFactory() {
		super(null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public TProcessor getProcessor(TTransport trans) {
		UCSImpl impl = new UCSImpl();
		Processor processor = new Processor(impl); 
		return processor;
	}

}
