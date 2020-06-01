
public enum State {
	
	// for the seek of simplicity, we divide the WAITING state into two states:
	// 1- WAITING_IO (waiting for I/O) , 2- WAITING_Mem (waiting for memory)
	READY, WAITING_IO,WAITING_Mem, RUNNING, TERMINATED, KILLED 
	
}
