import java.util.ArrayList;

// Job Class -> to keep trace of the information of a Job.
public class Job {  

	// used in long-term function
	public int load_ready; // When it was loaded into the ready queue.
	
	// filled when reading from the file
	public int process_id;
	public int arrival_time;
	public int first_mem; // first memory requirement from long-term
	
	
	ArrayList<burst> bursts; // all current bursts of the process
	
	// used in short_term function
	public int num_cpu; // Number of times it was in the CPU. 
	public int time_cpu; // Total time spent in the CPU
	public int num_io; // Number of times it performed an IO. 
	public int time_io; // Total time spent in performing IO
	public int num_wait; // Number of times it was waiting for memory. 
	public int num_preem; // Number of times its preempted (stopped execution because another process replaced it)
	public int time_TK;      // Time it terminated or was killed
	public State state;     // the processes will have one of the states: READY, WAITING, RUNNING, TERMINATED, KILLED. 
	public State final_state; // Its final state: Killed or Terminated
	public int addit_mem; // additional memory requirement
	
	public Job() {  // original constructor
		this.load_ready = 0;
		
		this.process_id = 0;
		this.arrival_time = 0;
		this.first_mem = 0;
		
		this.bursts = new ArrayList<burst>();
		
		this.num_cpu = 0; 
		this.time_cpu = 0; 
		this.num_io = 0; 
		this.time_io = 0; 
		this.num_wait = 0; 
		this.num_preem = 0; 
		this.time_TK = 0;      
		this.state = null;
		this.final_state = null; 
		this.addit_mem = 0;
	}
	
	public Job(Job e) { // copy constructor
		this.load_ready = e.load_ready;
		
		this.process_id = e.process_id;
		this.arrival_time = e.arrival_time;
		this.first_mem = e.first_mem;
		
		this.bursts = new ArrayList<burst>();
		for (int i = 0; i < e.bursts.size(); i++) {
			this.bursts.add(i, e.bursts.get(i));
		}
		
		this.num_cpu = e.num_cpu; 
		this.time_cpu = e.time_cpu; 
		this.num_io = e.num_io; 
		this.time_io = e.time_io; 
		this.num_wait = e.num_wait; 
		this.num_preem = e.num_preem; 
		this.time_TK = e.time_TK;      
		this.state = e.state;
		this.final_state = e.final_state; 
		this.addit_mem = e.addit_mem;
	}
     
}
