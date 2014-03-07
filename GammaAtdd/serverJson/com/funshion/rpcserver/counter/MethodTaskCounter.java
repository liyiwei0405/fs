package com.funshion.rpcserver.counter;

public class MethodTaskCounter{
	private final TaskCounter[]tasks;
	public MethodTaskCounter(String name, int[] watchItvs) {
		tasks = new TaskCounter[watchItvs.length + 1];
		for(int x = 0; x < tasks.length; x ++){
			tasks[x] = new TaskCounter(name, x == 0 ? 0 : watchItvs[x]);
		}
	}
	/**
	 * increment the number of serviced/servicing request.<br>
	 * @return
	 */
	public int incSericed(){
		for(TaskCounter t : tasks){
			t.incSericed();
		}
		return tasks[0]._serviced;
	}
	/**
	 * fail serviced, only for unexpected request
	 * @return
	 */
	public int incFailServiced(){
		for(TaskCounter t : tasks){
			t.incFailServiced();
		}
		return tasks[0]._failed;
	}
	/**
	 * ok serviced(understand and processed request number)
	 * @return
	 */
	public int incOkserviced(){
		for(TaskCounter t : tasks){
			t.inc200();
		}
		return tasks[0]._200;
	}

	/**
	 * reset numbers with timers
	 * @param nowSeconds
	 */
	public void reset(long nowSeconds){
		for(int x = 1; x < tasks.length; x ++){
			int itv = tasks[x].watchItv;
			if(itv <= 0){
				continue;
			}
			long mod = nowSeconds % itv;
			if(mod == 0){
				tasks[x].reset();
			}
		}
	}
}
