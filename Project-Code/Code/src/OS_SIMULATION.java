import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

// The class that have the implementation of the Project
public class OS_SIMULATION {

	public LinkedPQ readyQueue; // jobs that are ready to be running in the CPU. (Priority Queue by the burst
								// time of the processes)
	// for the seek of simplicity, we divide the waiting queue into two queues:
	public Queue<Job> waitingIOQueue; // 1- waiting for I/O
	public Queue<Job> waitingMemQueue; // 2- waiting for memory
	public Queue<Job> jobQueue;
	public Job runningJob; // the job that is running in the CPU
	public final int full_size = 1024;
	public int size85; // keep trace of how much used in RAM for 85% of the real size - MB
	public int size15; // 15% of the real size
	public int real_size; // size of the RAM without the OS usage
	public int clock;
	public ArrayList<Job> finaljobs; // final jobs results.
	public int numproc; // keep trace of the number of the processes in the system.
	public int numallproc; // number of the whole processes in the file .
	public double cpuUsage; // number of times of CPU usage in the system

	public OS_SIMULATION(int os_intake, int numOfProcesses) {
		real_size = full_size - os_intake;
		readyQueue = new LinkedPQ();
		waitingIOQueue = new Queue<Job>();
		waitingMemQueue = new Queue<Job>();
		jobQueue = new Queue<Job>();
		runningJob = null;
		numallproc = numOfProcesses;
		numproc = 0;
		clock = 0;
		size85 = 0;
		size15 = 0;
		cpuUsage = 0;
		finaljobs = new ArrayList<Job>();
	}

	// Simulating the system (Main function), that have every thing on it.
	// the function that will be called in the Main.
	public void Simulation(OS_SIMULATION sim) {
		String filename = "Write processes";
		sim.write(filename);
		sim.read(filename);

		sim.Short_Term();

		String Resultfile = "final results";
		sim.writeResults(Resultfile);
	}

	/*
	 * From ready queue to the CPU (by - SJRF scheduling algorithm) 1) Start the
	 * simulation run, which consists of a simulation of the Machine Execution
	 * Cycle. At each millisecond, the scheduler will check if a job CPU-burst has
	 * ended and if the I/O burst of a process has ended or a new process enters the
	 * ready Queue and its time is less than the remaining time of the currently
	 * running process (preemption). It should also check if any waiting process can
	 * be reactivated and put in the ready queue. 2) If a process requires
	 * additional memory and there is not enough memory to satisfy its request, it
	 * should be put in Waiting state until there is enough memory for it. 3) If all
	 * processes are in Waiting state, only if all waiting for memory allocation,
	 * this is a deadlock. The system should declare a deadlock and select the
	 * largest waiting process to be killed in order to get some free memory for the
	 * other processes.
	 */
	public void Short_Term() {
		int i;
		int num;
		while (finaljobs.size() != numallproc) { // do we finish all processes ?

			if (waitingMemQueue.length() == numproc) // deadLock condition
				deadLock();

			if (clock % 200 == 0) { // wake-up call for long-term
				Long_Termfill();
			}
			// SJRF scheduling algorithm
			// 1) take off the earliest job from the ready queue and check if it has less
			// CPU time than runningJob.

			if (runningJob != null) { // surly there is a running Job in the CPU
				if (readyQueue.length() >= 1) {
					Job checkJob = readyQueue.earliestNode(clock);
					if (checkJob != null) {
						if (checkJob.bursts.get(0).time < runningJob.bursts.get(0).time) {
							readyQueue.deleteSpec(checkJob);
							PQNode wasrunning = new PQNode(runningJob, runningJob.bursts.get(0).time);
							wasrunning.data.num_preem++;
							wasrunning.data.state = State.READY;
							readyQueue.enqueue(wasrunning.data, wasrunning.data.bursts.get(0).time);
							checkJob.num_cpu++;
							runningJob = checkJob;
							checkJob.state = State.RUNNING;
						}
					}
				}
			} else { // there is no running job at clock time
				if (readyQueue.length() >= 1) {
					Job checkJob = readyQueue.earliestNode(clock);
					if (checkJob != null) {
						readyQueue.deleteSpec(checkJob);
						checkJob.num_cpu++;
						checkJob.state = State.RUNNING;
						runningJob = checkJob;
					}
				}
			}
			// 2) check if one of the waiting IO processes has done waiting to send to the
			// ready queue.
			num = waitingIOQueue.length();
			for (i = 0; i < num; i++) {
				Job io = waitingIOQueue.serve();
				int time = io.bursts.get(0).time;
				if (time == 0) { // has it finished its IO ? If yes, send it to the ready queue
					io.bursts.remove(0);
					int mem = io.bursts.get(0).memory_inst;
					if (mem <= 0) {
						size15 += mem; // will free some memory
						io.addit_mem += mem;
						readyQueue.enqueue(io, io.bursts.get(0).time);
					} else if (size15 + mem <= size_15()) {
						size15 += mem;
						io.addit_mem += mem;
						readyQueue.enqueue(io, io.bursts.get(0).time);
					} else
						waitingMemQueue.enqueue(io);
				} else {
					waitingIOQueue.enqueue(io);
				}
			}
			// 3) check the processes in waiting memory queue, if we have available space
			// (15% - additional memory requirement) or not,
			// if yes, we send it back to the ready queue and increment the size15, if no
			// it's still waiting in the waiting memory queue.
			num = waitingMemQueue.length();
			for (i = 0; i < num; i++) {
				Job memJob = waitingMemQueue.serve();
				int mem = memJob.bursts.get(0).memory_inst;
				if (size15 + mem <= size_15()) {
					size15 += mem;
					memJob.addit_mem += mem;
					readyQueue.enqueue(memJob, memJob.bursts.get(0).time);
				} else {
					memJob.num_wait++;
					waitingMemQueue.enqueue(memJob);
				}
			}

			// 4) after finishing the CPU burst in the running job,
			// we check if it has other bursts ( IO .. CPU ... ) or it will be terminated
			// (-1) and decrement its memory from size (85%) & (15%).
			if (runningJob != null) {
				if (runningJob.bursts.get(0).time == 0) {
					if (runningJob.bursts.size() > 1) { // is there other bursts in the process ?
						runningJob.bursts.remove(0);
						Job toIoJob = new Job(runningJob);
						toIoJob.num_io++;
						waitingIOQueue.enqueue(toIoJob);
					} else {
						runningJob.bursts.remove(0);
						Job finishedJob = new Job(runningJob);
						finishedJob.final_state = State.TERMINATED;
						finishedJob.time_TK = clock;
						numproc--;
						size85 -= finishedJob.first_mem;
						size15 -= finishedJob.addit_mem;
						finaljobs.add(finishedJob);
					}
					runningJob = null;
				}
			}

			// processing the IO queue Jobs.
			num = waitingIOQueue.length();
			for (i = 0; i < num; i++) {
				Job ioJob = waitingIOQueue.serve();
				ioJob.bursts.get(0).time--;
				ioJob.time_io++;
				waitingIOQueue.enqueue(ioJob);
			}

			// processing the running job
			if (runningJob != null) {
				cpuUsage++;
				runningJob.time_cpu++;
				runningJob.bursts.get(0).time--;
			}

			clock++;
		}
	}

	/*
	 * The system should declare a deadlock and select the largest waiting process
	 * to be killed in order to get some free memory for the other processes.
	 */
	public void deadLock() {
		int num = waitingMemQueue.length();
		Job max = null;
		boolean flag = false;
		if (num >= 1) {
			max = waitingMemQueue.peak();
			flag = true;
		}
		for (int i = 0; i < num; i++) {
			Job job = waitingMemQueue.serve();
			if (job.bursts.get(0).memory_inst > max.bursts.get(0).memory_inst)
				max = job;
			waitingMemQueue.enqueue(job);

		}
		if (flag) {
			numproc--;
			Job maxJob = new Job(max);
			waitingMemQueue.deleteSpec(max);
			maxJob.time_TK = clock;
			maxJob.final_state = State.KILLED;
			size85 -= maxJob.first_mem;
			size15 -= maxJob.addit_mem;
			finaljobs.add(maxJob);
		}
	}

	// CPU utilization in the system
	public double CPU_util() {
		return (cpuUsage / clock) * 100;
	}

	// Load the jobs to the RAM if there is a space
	public void Long_Termfill() {
		int num = jobQueue.length();
		for (int i = 0; i < num; i++) {
			Job temp = jobQueue.serve();
			int mem = temp.bursts.get(0).memory_inst;
			if (size85 + mem <= size_85()) {
				size85 += mem;
				numproc++;
				temp.first_mem = mem;
				temp.load_ready = clock;
				temp.state = State.READY;
				readyQueue.enqueue(temp, temp.bursts.get(0).time);
			} else
				jobQueue.enqueue(temp);
		}
	}

	public boolean full() {
		return size85 + size15 == real_size;
	}

	// 15% of the the real size in the system.
	public double size_15() {
		return (real_size * 0.15);
	}

	// 85% of the the real size in the system.
	public double size_85() {
		return (real_size * 0.85);
	}

	// Read the jobs from the file and load it into the job queue.
	public void read(String filename) {

		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);

			int indexburst;
			String line;
			int id = 1;
			Job sortJobs[] = new Job[numallproc];
			// this loop go through all processes
			for (int i = 0; i < sortJobs.length; i++) {
				indexburst = 0;
				// make a new Job
				Job job = new Job();
				job.process_id = id;
				sortJobs[i] = job;
				// this loop go through a single Job
				line = br.readLine();
				String info[] = line.split(" ");
				sortJobs[i].arrival_time = Integer.parseInt(info[0]);
				for (int j = 1; j < info.length - 1;) {
					// filling CPU information
					burst cpuBurst = new burst();
					cpuBurst.time = Integer.parseInt(info[j++]);
					cpuBurst.memory_inst = Integer.parseInt(info[j++]);
					cpuBurst.cpu = true;
					sortJobs[i].bursts.add(indexburst++, cpuBurst);

					if (!(j < info.length - 1))
						break;

					// filling IO information
					burst ioBurst = new burst();
					ioBurst.time = Integer.parseInt(info[j++]);
					ioBurst.memory_inst = -1;
					ioBurst.cpu = false;
					sortJobs[i].bursts.add(indexburst++, ioBurst);

				}
				id++;
			}

			// Sorting the jobs by arrival time
			SortAlgorithm sort = new SortAlgorithm();
			sort.mergeSort(sortJobs, 0, sortJobs.length - 1);

			// filling the job queue
			for (int i = 0; i < sortJobs.length; i++) {
				jobQueue.enqueue(sortJobs[i]);
			}

			br.close();
			fr.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// write the processes that the user want on a file Randomly.
	public void write(String filename) {
		Random rand = new Random();

		int cpu;
		int memor;
		int arri;
		int io;
		int memfree; // free some memory of the first memory requirement

		try {
			FileWriter fw = new FileWriter(filename);
			PrintWriter pw = new PrintWriter(fw);

			for (int i = 0; i < numallproc; i++) { // how many processes to generate
				memfree = 0;
				arri = RandRange(rand, 1, 80);
				pw.print(arri);
				int inner = RandRange(rand, 1, 3);  // you can change it if you want (we put an arbitrary values)
				for (int j = 0; j < inner; j++) {
					cpu = RandRange(rand, 10, 100);
					memor = RandRange(rand, 5, 200);
					io = RandRange(rand, 20, 60);

					pw.print(" " + cpu + " " + memor + " ");
					pw.print(io);
					if (j != 0)
						memfree += memor;
				}
				cpu = RandRange(rand, 10, 100);
				memor = RandRange(rand, 0, memfree); // make a free or no change instruction in the memory
				memor = -1 * memor;
				pw.print(" " + cpu + " " + memor + " ");
				pw.println("-1");

			}
			pw.close();
			fw.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// write the results of the simulation on a file.
	public void writeResults(String filename) {
		try {
			FileWriter fw = new FileWriter(filename);
			PrintWriter pw = new PrintWriter(fw);

			for (int i = 0; i < finaljobs.size(); i++) {
				pw.println("Process: ");
				Job currentJob = finaljobs.get(i);
				pw.println("Process ID -> " + currentJob.process_id);
				pw.println("When it was loaded into the ready queue -> " + currentJob.load_ready);
				pw.println("Number of times it was in the CPU -> " + currentJob.num_cpu);
				pw.println("Total time spent in the CPU -> " + currentJob.time_cpu);
				pw.println("Number of times it performed an IO -> " + currentJob.num_io);
				pw.println("Total time spent in performing IO -> " + currentJob.time_io);
				pw.println("Number of times it was waiting for memory -> " + currentJob.num_wait);
				pw.println("Number of times its preempted -> " + currentJob.num_preem);
				pw.println("Time it terminated or was killed -> " + currentJob.time_TK);
				pw.println("Its final state: Killed or Terminated -> " + currentJob.final_state);
				pw.println("-------------------------------------------------------------");

			}
			pw.println("CPU Utilization -> " + CPU_util() + " %");
			pw.close();
			fw.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// give a random number between the range of min and max.
	private int RandRange(Random r, int min, int max) {
		return r.nextInt((max - min) + 1) + min;
	}
}