# WaterFactory Simulation

Write a Java program that simulates a Water manufacturing factory.

Your factory has infinitely capable Hydrogen and Oxygen producer units (independent units) provided free of charge by the government.

Your pipeline (or consumer) should consume 2 atoms of Hydrogen and one atom of Oxygen to generate one molecule of Water.

## There are 3 things to note:



1. **No wastage of Hydrogen or Oxygen**

2. **The size of the pipes that take the output of both the Hydrogen and Oxygen producers is limited in length and can hold only 500 atoms at a time.**

3. **The pipeline consumer that combines Hydrogen and Oxygen will take 5 seconds to generate 1 molecule of Water.**

Write a multi-threaded program to simulate the producer and consumer. Your factory’s output should be 10 molecules of Water per second

## Problem Breakdown

1. **Hydrogen Production**:
    - Hydrogen atoms are produced at a rate of **1 atom every 50 milliseconds**.
    - The Hydrogen producer continues to add Hydrogen atoms to a **bounded queue** (with a maximum capacity of 500) to prevent wastage.

2. **Oxygen Production**:
    - Oxygen atoms are produced at a rate of **1 atom every 150 milliseconds**.
    - The Oxygen producer adds Oxygen atoms to another **bounded queue** (with a maximum capacity of 500).

3. **Water Pipeline Consumer**:
    - The consumer retrieves **2 Hydrogen atoms** and **1 Oxygen atom** from the queues to produce **1 water molecule**.
    - The process of combining these atoms takes **5 seconds**.
    - The consumer is triggered every **100 milliseconds** to ensure that **10 molecules** are produced per second.

### Design Constraints:
- **No wastage** of Hydrogen or Oxygen atoms. The producers will not add atoms to the queue if the queue is already full.
- The pipeline will produce exactly **10 water molecules per second**.
- **BlockingQueues** are used for managing the atom queues and ensuring that production is synchronized between producers and consumers.

## System Overview

### 1. **Hydrogen Atom Producer**

The `HydrogenAtomProducer` is responsible for producing Hydrogen atoms and placing them into a queue (`hydrogenQueue`). It simulates production by adding an atom every 50 milliseconds. The producer will wait if the queue has reached its maximum capacity (500 atoms).

### 2. **Oxygen Atom Producer**

The `OxygenAtomProducer` is responsible for producing Oxygen atoms and placing them into a queue (`oxygenQueue`). It simulates production by adding an atom every 150 milliseconds. Like the Hydrogen producer, it waits if the queue reaches its maximum capacity (500 atoms).

### 3. **Water Pipeline Consumer**

The `WaterPipelineConsumer` is responsible for consuming atoms from the queues and generating water molecules. To create one molecule of water, it consumes:
- 2 Hydrogen atoms
- 1 Oxygen atom

The consumer simulates the time taken to produce 1 molecule of water by sleeping for 5 seconds. After that, it logs the time when the molecule was created. The consumer is triggered every 100 milliseconds to ensure that 10 molecules are produced per second.

### 4. **Thread Management and Scheduling**

- **Producers (Hydrogen and Oxygen)** are created as separate threads to simulate continuous production.
- A **FixedThreadPool** is used to manage consumer threads, ensuring a pool of workers that can handle the task of producing 10 molecules per second.
- A **ScheduledExecutorService** is used to schedule consumer tasks at fixed intervals (every 100 milliseconds). This ensures that the consumer is triggered at regular intervals to maintain the desired production rate of 10 molecules per second.

## Threads Calculation 
         To simulate the exact creation of 10 water molecules per second, we need to balance the consumer tasks
         that will consume Hydrogen and Oxygen atoms and combine them into water molecules.

         Each water molecule takes 5 seconds to create, and we want 10 molecules per second.
        
               1 (Thread)
           ---------------- = 0.2 (water Molecule per second)
               5 (sec)
        
             10(Threads)
           ---------------- = 2 (water Molecule per second) 
               5 (sec)
        
             10(Threads)
           ---------------- = 2X 5(sec) =10 (water Molecule per second) 
               5 (sec)
        
        
         We will use a scheduling strategy that triggers 1 molecule every 100ms to meet the required output.
          # Output Production Over Time

In this table, we simulate how the output (water molecules) is produced over time based on the number of threads in the system and the time it takes for each thread to produce a molecule.

| **Time (Seconds)** | **Threads Producing Output** | **Water Molecules Produced** | **Cumulative Water Molecules** | **Explanation**                                          |
|--------------------|------------------------------|------------------------------|-------------------------------|----------------------------------------------------------|
| 1                  | 10                           | 2                            | 2                             | 10 threads start producing output. At 1 second, 2 molecules are produced. |
| 2                  | 10                           | 2                            | 4                             | By the end of second 2, another 2 molecules are produced. |
| 3                  | 10                           | 2                            | 6                             | By the end of second 3, another 2 molecules are produced. |
| 4                  | 10                           | 2                            | 8                             | By the end of second 4, another 2 molecules are produced. |
| 5                  | 10                           | 2                            | 10                            | By the end of second 5, another 2 molecules are produced. |
| 6                  | 50                           | 10                           | 20                            | After 5 seconds, 50 threads are producing output. Each second, 10 molecules are produced. |
| 7                  | 50                           | 10                           | 30                            | By the end of second 7, 10 more molecules are produced. |
| 8                  | 50                           | 10                           | 40                            | By the end of second 8, 10 more molecules are produced. |
| 9                  | 50                           | 10                           | 50                            | By the end of second 9, 10 more molecules are produced. |
| 10                 | 50                           | 10                           | 60                            | By the end of second 10, 10 more molecules are produced. |
| 11                 | 50                           | 10                           | 70                            | By the end of second 11, 10 more molecules are produced. |
| 12                 | 50                           | 10                           | 80                            | By the end of second 12, 10 more molecules are produced. |
| 13                 | 50                           | 10                           | 90                            | By the end of second 13, 10 more molecules are produced. |
| 14                 | 50                           | 10                           | 100                           | By the end of second 14, 10 more molecules are produced. |
| 15                 | 50                           | 10                           | 110                           | By the end of second 15, 10 more molecules are produced. |

---

### Key Points:

- **1st to 5th second**: Initially, 10 threads are used to produce 2 molecules every second. By the end of the 5th second, 10 molecules have been produced.

- **6th second and beyond**: After 5 seconds, the number of threads increases to 50, producing 10 molecules per second. This results in a continuous production of water molecules at a rate of 10 per second.

This pattern continues indefinitely, with a constant output of 10 molecules every second from the 6th second onward.

.

## Flow of Execution

1. **Start Producers**:
    - Hydrogen and Oxygen producers run in separate threads and continuously add atoms to their respective queues.

2. **Water Pipeline Consumer**:
    - The `WaterPipelineConsumer` consumes Hydrogen and Oxygen atoms from the queues to create a water molecule.
    - Every 100 milliseconds, a new consumer thread is triggered, ensuring that 10 molecules are produced per second.

3. **Atomic Queue Management**:
    - The Hydrogen and Oxygen queues are both bounded, each with a capacity of 500 atoms. If the queue is full, the producer waits until there is space available to add new atoms.

4. **Water Production**:
    - Each time the consumer successfully consumes 2 Hydrogen atoms and 1 Oxygen atom, it simulates a 5-second process for creating a water molecule.
    - After producing the molecule, the consumer logs the current timestamp to show when the molecule was created.

5. **Thread Coordination**:
    - The system uses **BlockingQueue** to synchronize producers and consumers, ensuring proper coordination of atom consumption and molecule production.


