
class PQNode {
	public Job data;
	public int priority;
	public PQNode next;

	public PQNode() {
		next = null;
	}

	public PQNode(Job e, int p) {
		data = e;
		priority = p;
	}
}

// to be used for the implementation of the ready queue.
// ----- Priority Queue ---------
public class LinkedPQ {
	private int size;
	private PQNode head;

	public LinkedPQ() {
		head = null;
		size = 0;
	}

	public int length() {
		return size;
	}

	// return the data of the head of the Priority Queue without deleting it
	public Job peak() {
		return head.data;
	}

	// delete a specific element in the Priority Queue
	public void deleteSpec(Job minTime) { 

		if (minTime.equals(head.data))
			head = head.next;
		else {
			PQNode temp = head;
			while (temp.next.data != minTime) {
				temp = temp.next;
			}
			temp.next = temp.next.next;
		}
		size--;
	}

	// return the minimum time burst in the range of clock
	public Job earliestNode(int clock) {
		PQNode current = head;
		PQNode min = null;
		boolean flag = false;
		while (current != null) {
			if (!flag && current.data.arrival_time <= clock) {
				min = current;
				flag = true;
			}
			if (flag && current.data.bursts.get(0).time < min.data.bursts.get(0).time && current.data.arrival_time <= clock)
				min = current;
			
			current = current.next;
		}
		if(min == null)
			return null;
		return min.data;
	}

	public void enqueue(Job e, int pty) {
		PQNode tmp = new PQNode(e, pty);
		if ((size == 0) || (pty > head.priority)) {
			tmp.next = head;
			head = tmp;
		} else {
			PQNode p = head;
			PQNode q = null;
			while ((p != null) && (pty <= p.priority)) {
				q = p;
				p = p.next;
			}
			tmp.next = p;
			q.next = tmp;
		}
		size++;
	}

	public Job serve() {
		PQNode node = head;
		head = head.next;
		size--;
		return node.data;
	}

}
