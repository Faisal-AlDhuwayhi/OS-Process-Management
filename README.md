# Opereating System - Process Management
# 1 Objective
This project simulates the behavior of the multiprogramming operating system and use CPU scheduler, and CPU Execution. At the end of the simulation, it's expected to output some statistics regarding the behavior of the system.

# 2 Specification
You can find the hardware specification, the multiprogramming OS features and the jobs requirements and more in [this link](Specification.pdf) .


# 3 Output Of The Project
Output from the simulation:
A text file containing statistics about all processes and their final status TERMINATED or KILLED. Statistics about a process should contain:
1.	Process ID
1.	When it was loaded into the ready queue
1.	Number of times it was in the CPU
1.	Total time spent in the CPU
1.	Number of times it performed an IO 
1.	Total time spent in performing IO
1.	Number of times it was waiting for memory 
1.	Number of times its preempted (stopped execution because another process replaced it)
1.	Time it terminated or was killed
1.	Its' final state: Killed or Terminated

- And should also output the **CPU Utilization** of the system .

