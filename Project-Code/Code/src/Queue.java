
class NodeQ<T> {
	public T data;
	public NodeQ<T> next;
	
	public NodeQ() {
		data = null;
		next = null;
	}
	
	public NodeQ(T val) {
		data = val;
		next = null;
	}

	
}
//to be used for the implementation of the waiting queue and the job queue.
//----- Queue ---------
public class Queue<T>{
	private NodeQ<T> head, tail;
	private int size;
	
	/** Creates a new instance of LinkedQueue */
	public Queue() {
		head = tail = null;
		size = 0;
	}
	
	public T peak() {
		return head.data;
	}
	
	public int length (){
		return size;
	}
	
	public void deleteSpec(T e) { // delete a specific element in the Queue by its reference of data
		if (e == head.data) {
			head = head.next;
		}
		else {
			NodeQ<T> temp = head;
			while (temp.next.data != e) {
				temp = temp.next;
			}
			temp.next = temp.next.next;
			if(tail.data == e)
				tail = temp;
		}
		size--;
		if(size == 0)
			tail = null;
	}

	public void enqueue(T e) {
		if(tail == null){
			head = tail = new NodeQ<T>(e);
		}
		else {
			tail.next = new NodeQ<T>(e);
			tail = tail.next;
		}
		size++;
	}

	public T serve() {
		T x = head.data;
		head = head.next;
		size--;
		if(size == 0)
			tail = null;
		return x;
	}

}


