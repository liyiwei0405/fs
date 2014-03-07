package com.funshion.search;
public class CounterNum {
		private int count;
		public CounterNum(){
			this.count = 1;
		}
		CounterNum(int count){
			this.count = count;
		}
		
		public boolean equals(Object o){
			if(o == null || !(o instanceof Counter<?>))			
				return false;
			return count == ((CounterNum)o).count;
		}
		
		public int hashCode(){
			return count;
		}
		public void increaseCount(){
			count ++;
		}
		public void increaseCount(int num){
			count += num;
		}
		public String toString(){
			return String.valueOf(count);
		}
		public int intValue(){
			return this.count;
		}
	}