package com.funshion.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Counter<T> {
	
	protected Map<T, CounterNum>map = Collections.synchronizedMap(new HashMap<T, CounterNum>());

	public Counter(){}

	public int count(T key){
		return this.count(map, key, 1);
	}
	public int count(T key, int num){
		return this.count(map, key, num);
	}
	public int count(T key, CounterNum num){
		return this.count(map, key, num.intValue());
	}
	public void count(Map<T, CounterNum>map,T key, CounterNum num){
		count(map, key, num.intValue());
	}
	public int count(Map<T, CounterNum>map,T key, int num){
		CounterNum cnt = map.get(key);
		if(cnt == null){
			map.put(key, new CounterNum(num));
			return num;
		}else{
			cnt.increaseCount(num);
			return num + cnt.intValue();
		}
	}
	public List<Entry<T, CounterNum>> topEntry(int num){
		return this.topEntry(map, num);
	}
	
	public List<T>topObjects(int num){
		List<Entry<T, CounterNum>> lst = this.topEntry(map, num);
		List<T> l = new ArrayList<T>(lst.size());
		for(Entry<T, CounterNum> e : lst ) {
			l.add(e.getKey());
		}
		return l;
	}
	public List<Entry<T, CounterNum>> topEntry(Map<T, CounterNum> map, int num){
		ArrayList<Entry<T, CounterNum>> lst =new ArrayList<Entry<T, CounterNum>>();
		lst.addAll(map.entrySet());

		Collections.sort(lst,new Comparator<Entry<T, CounterNum>>(){
			@Override
			public int compare(Entry<T, CounterNum> o1,
					Entry<T, CounterNum> o2) {
				return o2.getValue().intValue() - o1.getValue().intValue();
			}
		}
		);
		int range = lst.size() > num ? num : lst.size();

		return 	lst.subList(0, range);
	}
	
	public Map<T, CounterNum>getMap(){
		return this.map;
	}

	public String toString() {
		return this.map.toString();
	}

	public void renew() {
		this.map.clear();
	}
	public int size(){
		return map.size();
	}
}
