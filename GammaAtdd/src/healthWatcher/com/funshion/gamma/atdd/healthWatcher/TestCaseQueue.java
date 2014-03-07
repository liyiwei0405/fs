package com.funshion.gamma.atdd.healthWatcher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import com.funshion.search.utils.LogHelper;

class TestCaseQueue{
	final LogHelper log;
	private Map<String, NamedGammaTestCase>map = new HashMap<String, NamedGammaTestCase>();
	private final LinkedBlockingQueue<String> nameQueue = new LinkedBlockingQueue<String>();
	TestCaseQueue(String serviceName){
		log = new LogHelper("queue-" + serviceName);
	}
	public synchronized NamedGammaTestCase take() throws InterruptedException{
		while(true){
			String name = nameQueue.take();
			NamedGammaTestCase testCase = map.get(name);
			if(testCase == null){
				log.warn("STRANGE! testcase %s is missing", name);
				continue;
			}else{
				return testCase;
			}
		}
	}
	/**
	 * put back the old testcase to queue
	 * @param testCase
	 * @throws Exception
	 */
	protected synchronized void putBack(NamedGammaTestCase testCase) throws Exception{
		NamedGammaTestCase oldCase = map.get(testCase.name);
		if(oldCase == null){
			log.warn("skip old testCase %s",  testCase);
			return;
		}
		nameQueue.put(testCase.name);
		if(oldCase.md5Flag.equals(testCase.md5Flag)){//FIXME
			log.debug("skip testcase! md5 match! %s", testCase.name);
		}else{
			log.warn("mismatched md5flag for old testCase %s",  testCase);
		}
	}
	public synchronized void mergeNewTestCases(List<NamedGammaTestCase> testCases) throws Exception{
		Map<String, NamedGammaTestCase>newMap = 
				new HashMap<String, NamedGammaTestCase>();

		for(NamedGammaTestCase testCase : testCases){
			newMap.put(testCase.name, testCase);
			NamedGammaTestCase oldCase = map.get(testCase.name);
			map.remove(testCase.name);

			if(oldCase == null){
				log.warn("add new testCase %s with md5Flag %s",  testCase.name, testCase.md5Flag);
				log.debug("new added testcase detail:%s", testCase);
				nameQueue.put(testCase.name);
			}else{
				if(!oldCase.md5Flag.equals(testCase.md5Flag)){
					log.warn("refresh md5flag for old testCase %s to %s", 
							oldCase, testCase);
				}
			}
		}
		for(Iterator<NamedGammaTestCase>itr = map.values().iterator(); itr.hasNext();){
			NamedGammaTestCase tCase = itr.next();
			log.warn("removed old testCase %s", 
					tCase);
		}
		this.map = newMap;
	}
}