
public class Main {

	public static void main(String[] args) {
		// 320 -> os_intake of the memory
		// 1000 -> number of processes in the system
		OS_SIMULATION sim = new OS_SIMULATION(320, 100);
		
		// start simulation
		sim.Simulation(sim);
		
		
	}

	
}
