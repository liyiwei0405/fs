package com.funshion.rpcserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MethodTaskCounter{
	private final TaskCounter[]tasks;
	
	public MethodTaskCounter(String name, int[] watchItvs) {
		tasks = new TaskCounter[watchItvs.length + 1];
		tasks[0] = new TaskCounter(name, 0);
		for(int x = 0; x < watchItvs.length; x ++){
			tasks[x + 1] = new TaskCounter(name, watchItvs[x]);
		}
	}
	
	public List<Map<String, Object>> getTasksAsMap() {
		List<Map<String, Object>> taskMapList = new ArrayList<Map<String, Object>>(this.tasks.length);
		for(TaskCounter task : this.tasks){
			taskMapList.add(task.addToMap());
		}
		return taskMapList;
	}

	public long incServiced(){
		for(TaskCounter t : tasks){
			t.incServiced();
		}
		return tasks[0]._serviced.longValue();
	}
	
	public long incFailServiced(long usedNanoSec){
		for(TaskCounter t : tasks){
			t.incFailServiced(usedNanoSec);
		}
		return tasks[0]._failed.longValue();
	}
	
	public long incOkServiced(long usedNanoSec){
		for(TaskCounter t : tasks){
			t.incOkServiced(usedNanoSec);
		}
		return tasks[0]._Ok.longValue();
	}
	
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
	
	@Override
	public String toString(){
		return this.tasks.toString();
	}
}
