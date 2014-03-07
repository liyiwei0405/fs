package test.bishi;

import java.util.LinkedList;
import java.util.Queue;


public class BinaryTree {
	Queue<BNode> queue = new LinkedList<BNode>();
	
	public class BNode{
		Object val;
		BNode lChild;
		BNode rChild;
	}
	
	public void process(BNode node){
		System.out.println(node.toString());
	}
	
	public void unRecurDFS(BNode node){
		
	}
	
	public void DFS(BNode node){
		process(node);
		if(node.lChild != null){
			process(node.lChild);
		}
		if(node.rChild != null){
			process(node.rChild);
		}
	}
	
	public void BFS(BNode node){
		this.queue.add(node);
		while(! queue.isEmpty()){
			BNode n = queue.remove();
			if(node.lChild != null){
				queue.add(node.lChild);
			}
			if(node.rChild != null){
				queue.add(node.rChild);
			}
			process(n);
		}
	}
	
	public static void main(String[] args) throws Exception {

	}

}