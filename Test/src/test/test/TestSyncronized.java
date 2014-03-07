package test;

import java.text.ParseException;
import java.util.concurrent.LinkedBlockingQueue;


public class TestSyncronized{
	class Take extends Thread{
		Queue q;
		
		@Override
		public void run() {
			try {
				this.q.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	class Put extends Thread{
		Queue q;
		
		@Override
		public void run() {
			try {
				this.q.put();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public class Queue{
		LinkedBlockingQueue<String> nameQueue = new LinkedBlockingQueue<String>(10);

		public synchronized void get() throws Exception{
			System.out.println("taking...");
			String n = nameQueue.take();
			System.out.println("already take: " + n);
		}

		public synchronized void put() throws Exception{
			nameQueue.put("abc");
			System.out.println("already put");
		}
	}
	
	public void work(){
		Queue q = new Queue();
		
		Take take = new Take();
		take.q = q;
		take.start();
		
		Put put = new Put();
		put.q = q;
		put.start();
	}
	
	public static void main(String[] args) throws ParseException, Exception {
		TestSyncronized t = new TestSyncronized();
		t.work();
	}

}
