package com.funshion.gamma.atdd;

import java.lang.reflect.Constructor;

import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.transport.TTransport;

import com.funshion.search.utils.systemWatcher.SSDaemonService;

public abstract class AbstractThriftService extends SSDaemonService{
	public static final String IfaceDepector = "$Iface";
	public final int servicePort;
	public AbstractThriftServerInner server;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public AbstractThriftService(int servicePort, Class svcClass, Object ifaceImp)
			throws Exception {
		super(servicePort % 1000 + 5000, 
				svcClass.getName().split("\\.")[svcClass.getName().split("\\.").length - 1], false);
		this.servicePort = servicePort;
		final Class ifaceClass = Class.forName(svcClass.getName() + IfaceDepector);
		final Class processorClass = Class.forName(svcClass.getName() + "$Processor");
		final Constructor procCons = processorClass.getConstructor(ifaceClass);
		final TProcessor processorImp =  (TProcessor) procCons.newInstance(ifaceImp);

		final TProcessorFactory fac = new TProcessorFactory(null){
			@Override
			public TProcessor getProcessor(TTransport trans) {
				return processorImp;
			}
		};
		server = new AbstractThriftServerInner(servicePort, 3000){

			@Override
			public TProcessorFactory getProcessorFacotry() {
				return fac;
			}

		};
	}
	/**
	 * init the system, such as preload datas/init instance
	 * @throws Exception
	 */
	protected abstract void init() throws Exception;

	@Override
	protected final void work(Object[] paras) throws Exception {
		try{
			logd.debug("initing" );
			init();
			logd.debug("inited! starting server");
		}catch(Exception e){
			e.printStackTrace();
			Thread.sleep(500);
			System.exit(0);
		}
		server.startService();
	}
}
