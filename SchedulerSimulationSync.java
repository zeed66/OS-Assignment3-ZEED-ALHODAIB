import java.util.LinkedList;
import java.util.Queue;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Semaphore;

class Colors {
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String BLUE = "\u001B[34m";
    public static final String RED = "\u001B[31m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_WHITE = "\u001B[97m";
    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_GREEN = "\u001B[42m";
    public static final String WHITE = "\u001B[37m";
}

class SharedResources {
    public static int contextSwitchCount = 0;
    public static int completedProcessCount = 0;
    public static long totalWaitingTime = 0;
    public static List<String> executionLog = new ArrayList<>();
    
    public static final ReentrantLock lock = new ReentrantLock();
    public static final Semaphore cpuSemaphore = new Semaphore(1);

    public static void incrementContextSwitch() {
        lock.lock();
        try { contextSwitchCount++; } finally { lock.unlock(); }
    }

    public static void incrementCompletedProcess() {
        lock.lock();
        try { completedProcessCount++; } finally { lock.unlock(); }
    }

    public static void addWaitingTime(long time) {
        lock.lock();
        try { totalWaitingTime += time; } finally { lock.unlock(); }
    }

    public static void logExecution(String message) {
        lock.lock();
        try { executionLog.add(message); } finally { lock.unlock(); }
    }
}

class Process implements Runnable {
    private String name;
    private int burstTime;
    private int timeQuantum;
    private int remainingTime;
    private long creationTime;
    private long startTime;
    private long completionTime;
    private int priority;
    
    public Process(String name, int burstTime, int timeQuantum, int priority) {
        this.name = name;
        this.burstTime = burstTime;
        this.timeQuantum = timeQuantum;
        this.remainingTime = burstTime;
        this.priority = priority;
        this.creationTime = System.currentTimeMillis();
        this.startTime = -1;
    }
    
    @Override
    public void run() {
        try {
            SharedResources.cpuSemaphore.acquire();
            
            if (startTime == -1) {
                startTime = System.currentTimeMillis();
            }
            
            SharedResources.incrementContextSwitch();
            int runTime = Math.min(timeQuantum, remainingTime);
            
            System.out.println(Colors.BRIGHT_GREEN + "  ▶️ " + name + " executing [" + runTime + "ms]" + Colors.RESET);
            SharedResources.logExecution(name + " started execution");
            
            try {
                int steps = 5;
                for (int i = 1; i <= steps; i++) {
                    Thread.sleep(runTime / steps);
                    System.out.print("\r  " + createProgressBar((i * 100) / steps, 15));
                }
                System.out.println();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            remainingTime -= runTime;
            
            if (remainingTime <= 0) {
                completionTime = System.currentTimeMillis();
                SharedResources.addWaitingTime((completionTime - creationTime) - burstTime);
                SharedResources.incrementCompletedProcess();
                SharedResources.logExecution(name + " completed");
                System.out.println(Colors.CYAN + "  ✓ " + name + " finished!" + Colors.RESET);
            }
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            SharedResources.cpuSemaphore.release();
        }
    }

    private String createProgressBar(int progress, int width) {
        int filled = (progress * width) / 100;
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < width; i++) {
            if (i < filled) bar.append(Colors.GREEN + "█" + Colors.RESET);
            else bar.append(Colors.WHITE + "░" + Colors.RESET);
        }
        bar.append("] ").append(progress).append("%");
        return bar.toString();
    }

    public void runToCompletion() {
        try {
            Thread.sleep(remainingTime);
            remainingTime = 0;
            completionTime = System.currentTimeMillis();
            SharedResources.addWaitingTime((completionTime - creationTime) - burstTime);
            SharedResources.incrementCompletedProcess();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isFinished() { return remainingTime <= 0; }
    public String getName() { return name; }
    public int getBurstTime() { return burstTime; }
    public int getPriority() { return priority; }
    public long getWaitingTime() { return (completionTime > 0) ? (completionTime - creationTime) - burstTime : 0; }
}

public class SchedulerSimulationSync {
    public static void main(String[] args) {
        int studentID = 445050289; 
        Random random = new Random(studentID);
        int timeQuantum = 2000 + random.nextInt(4) * 1000;
        int numProcesses = 10 + random.nextInt(11);
        
        Queue<Thread> processQueue = new LinkedList<>();
        Map<Thread, Process> processMap = new HashMap<>();
        List<Process> allProcesses = new ArrayList<>();
        
        System.out.println(Colors.BOLD + Colors.BG_BLUE + " STARTING SIMULATION (ID: " + studentID + ") " + Colors.RESET);

        for (int i = 1; i <= numProcesses; i++) {
            Process p = new Process("P" + i, timeQuantum/2 + random.nextInt(2 * timeQuantum), timeQuantum, 1 + random.nextInt(5));
            allProcesses.add(p);
            addProcessToQueue(p, processQueue, processMap);
        }
        
        while (!processQueue.isEmpty()) {
            Thread currentThread = processQueue.poll();
            currentThread.start();
            try {
                currentThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            Process p = processMap.get(currentThread);
            if (!p.isFinished()) {
                if (!processQueue.isEmpty()) {
                    addProcessToQueue(p, processQueue, processMap);
                } else {
                    p.runToCompletion();
                }
            }
        }
        printStatistics(allProcesses, studentID);
    }

    public static void addProcessToQueue(Process p, Queue<Thread> q, Map<Thread, Process> m) {
        Thread t = new Thread(p);
        q.add(t);
        m.put(t, p);
    }

    public static void printStatistics(List<Process> processes, int id) {
        System.out.println("\n" + Colors.BOLD + Colors.BG_GREEN + " FINAL STATISTICS (ID: " + id + ") " + Colors.RESET);
        System.out.println("Context Switches: " + SharedResources.contextSwitchCount);
        System.out.println("Average Waiting Time: " + (SharedResources.totalWaitingTime / processes.size()) + "ms");
    }
}
