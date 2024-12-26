package org.example;

import java.time.Instant;
import java.util.concurrent.*;

/**
 * The WaterFactory program simulates the process of producing water molecules
 * from Hydrogen and Oxygen atoms in a multithreaded environment. This solution
 * utilizes the producer-consumer pattern using BlockingQueues for Hydrogen and Oxygen,
 * and simulates the production of water molecules by consuming 2 Hydrogen and 1 Oxygen atom.
 */
public class WaterFactory {

    /**
     * FIXED_PIPE_CAPACITY SET TO 500 AS PER THE GIVEN PROBLEM STATEMENT.
     * THIS CAPACITY LIMIT IS APPLIED TO BOTH HYDROGEN AND OXYGEN ATOM QUEUES
     * TO AVOID THE WASTAGE OF ATOMS.
     */
    private static final int FIXED_PIPE_CAPACITY = 500;

    /**
     * BlockingQueue for Hydrogen atoms with a maximum capacity of 500.
     * This queue ensures that only the allowed amount of Hydrogen atoms
     * are produced and stored at a time.
     */
    private static final BlockingQueue<String> hydrogenAtomQueue = new ArrayBlockingQueue<>(FIXED_PIPE_CAPACITY);

    /**
     * BlockingQueue for Oxygen atoms with a maximum capacity of 500.
     * This queue ensures that only the allowed amount of Oxygen atoms
     * are produced and stored at a time.
     */
    private static final BlockingQueue<String> oxygenAtomQueue = new ArrayBlockingQueue<>(FIXED_PIPE_CAPACITY);

    /**
     * The HydrogenAtomProducer class simulates the production of Hydrogen atoms.
     * It adds one Hydrogen atom to the hydrogenQueue every 50 milliseconds.
     * If the queue reaches full capacity (500), it waits until there is space available.
     */
    static class HydrogenAtomProducer implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    // Produce one Hydrogen atom and put it in the queue.
                    hydrogenAtomQueue.put("H");
                    // Sleep for 50ms to simulate production time.
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                // Handle thread interruption
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * The OxygenAtomProducer class simulates the production of Oxygen atoms.
     * It adds one Oxygen atom to the oxygenQueue every 150 milliseconds.
     * If the queue reaches full capacity (500), it waits until there is space available.
     * Note: For simplicity, this producer produces single Oxygen atoms instead of O2 molecules.
     */
    static class OxygenAtomProducer implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    // Produce one Oxygen atom and put it in the queue.
                    oxygenAtomQueue.put("O");
                    // Sleep for 150ms to simulate production time.
                    Thread.sleep(150);
                }
            } catch (InterruptedException e) {
                // Handle thread interruption
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * The WaterPipelineConsumer class simulates the process of combining Hydrogen
     * and Oxygen atoms to create one water molecule. It consumes 2 Hydrogen atoms
     * and 1 Oxygen atom, then simulates the 5-second process to produce a water molecule.
     * This consumer operates in a separate thread, using BlockingQueue to ensure synchronization.
     */
    static class WaterPipelineConsumer implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    // Consume 2 Hydrogen atoms and 1 Oxygen atom.
                    //.take() is Thread Safe blocking Operation.
                    hydrogenAtomQueue.take();
                    hydrogenAtomQueue.take();
                    oxygenAtomQueue.take();

                    // Simulate the 5-second process to create one water molecule.
                    Thread.sleep(5000);

                    // Log the creation of a water molecule with a timestamp.
                    System.out.println("Water molecule created by " + Thread.currentThread().getName() + " time: " + Instant.now().toString());
                }
            } catch (InterruptedException e) {
                // Handle thread interruption
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Create and start the producer threads for Hydrogen and Oxygen atoms.
        Thread hydrogenProducer = new Thread(new HydrogenAtomProducer());
        Thread oxygenProducer = new Thread(new OxygenAtomProducer());
        hydrogenProducer.start();
        oxygenProducer.start();

        // Create a fixed thread pool with 50 threads to handle water molecule creation.
        ExecutorService fixedWaterFactoryThreadPool = Executors.newFixedThreadPool(50);

        // Create a ScheduledExecutorService to schedule consumer tasks at fixed intervals.
        ScheduledExecutorService pipelineScheduler = Executors.newScheduledThreadPool(1);

        // To simulate the exact creation of 10 water molecules per second, we need to balance the consumer tasks
        // that will consume Hydrogen and Oxygen atoms and combine them into water molecules.

        // Each water molecule takes 5 seconds to create, and we want 10 molecules per second.
        //
        //       1 (Thread)
        //   ---------------- = 0.2 (water Molecule per second)
        //       5 (sec)
        //
        //     50(Threads)
        //   ---------------- = 10 (water Molecule per second) This average out the task
        //       5 (sec)
        //
        // We will use a scheduling strategy that triggers 1 molecule every 100ms to meet the required output.

        int moleculeCreationInterval = 100; // 100 milliseconds for each scheduled task

        // Schedule consumer tasks to run every 100ms (to simulate 10 molecules per second).
        pipelineScheduler.scheduleAtFixedRate(() -> {
            fixedWaterFactoryThreadPool.submit(new WaterPipelineConsumer());
        }, 0, moleculeCreationInterval, TimeUnit.MILLISECONDS);

        // Shutdown hook to stop the pipelineScheduler and thread pool gracefully on application termination.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            pipelineScheduler.shutdown();
            fixedWaterFactoryThreadPool.shutdown();
            System.out.println("Shutting down...");
        }));
    }
}
