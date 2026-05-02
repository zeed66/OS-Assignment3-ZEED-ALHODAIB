# Assignment 3 - Complete Documentation

**Student Name**: [Zeed alhodaib]  
**Student ID**: [445050289]  
**Date Submitted**: [May 2 2026]

---

## 🎥 VIDEO DEMONSTRATION LINK (REQUIRED)

> **⚠️ IMPORTANT: This section is REQUIRED for grading!**
> 
> Upload your 3-5 minute video to your **PERSONAL Gmail Google Drive** (NOT university email).
> Set sharing to "Anyone with the link can view".
> Test the link in incognito/private mode before submitting.

**Video Link**: [Paste your personal Gmail Google Drive link here]

**Video filename**: `445050289]_Assignment3_Synchronization.mp4`

**Verification**:
- [ ] Link is accessible (tested in incognito mode)
- [ ] Video is 3-5 minutes long
- [ ] Video shows code walkthrough and commits
- [ ] Video has clear audio
- [ ] Uploaded to PERSONAL Gmail (not @std.psau.edu.sa)

---

## Part 1: Development Log (1 mark)

Document your development process with **minimum 3 entries** showing progression:

### Entry 1 - [2 May,5]What I implemented: Basic class structure and shared resource counters.
Challenges encountered: Managing shared variables without corruption.
How I solved it: Introduced a shared class SharedResources with static members.
Testing approach: Printed counter values after a single process run.
Time spent: 1 hour.

---

### Entry 2 - [2May, 6]
What I implemented: Thread synchronization using ReentrantLock and Semaphore.
Challenges encountered: Compiler errors related to bracket placement and illegal start of expressions.
How I solved it: Carefully restructured the class hierarchy and ensured all try-finally blocks were correctly closed.
Testing approach: Ran the program multiple times to check for race conditions.
Time spent: o.5 hours.

---

### Entry 3 - [Date, Time]
What I implemented: ANSI color output and final statistical report with Student ID.
Challenges encountered: Formatting the output table for multiple processes.
How I solved it: Used String.format to align columns and added specialized color codes for readability.
Testing approach: Final verification of mathematical accuracy (Total waiting time / Number of processes).
Time spent: 0.5 hours.
---

### Entry 4 - [Date, Time]
**What I implemented**: 

**Challenges encountered**: 

**How I solved it**: 

**Testing approach**: 

**Time spent**: 

---

### Entry 5 - [Date, Time]
**What I implemented**: 

**Challenges encountered**: 

**How I solved it**: 

**Testing approach**: 

**Time spent**: 

---

## Part 2: Technical Questions (1 mark)

### Question 1: Race Conditions
**Q**: Identify and explain TWO race conditions in the original code. For each:
- What shared resource is affected?
- Why is concurrent access a problem?
- What incorrect behavior could occur?

**Your Answer**:

The shared counter contextSwitchCount was changed by several threads in the original code without protection, which resulted in lost updates when two threads increased it at the same time. There was also a race situation in the executionLog list that may result in a ConcurrentModificationException if two threads inserted messages simultaneously. Program crashes and inaccurate simulation results would arise from these problems.

---

### Question 2: Locks vs Semaphores
**Q**: Explain the difference between ReentrantLock and Semaphore. Where did you use each in your code and why?

**Your Answer**:

A mutual exclusion lock (mutex) called a reentrant lock makes sure that only one thread can access a certain code block at a time. I used it to keep the logs and counters safe. To ensure that only one process can be "running" even though several threads are ready, I utilized a semaphore with one permit to represent the CPU.

---

### Question 3: Deadlock Prevention
**Q**: What is deadlock? Explain TWO prevention techniques and what you did to prevent deadlocks in your code.

**Your Answer**:

[When threads wait indefinitely for one another to release locks, deadlock occurs. By utilizing try-finally blocks to ensure lock release in every scenario, I was able to avoid issue. In order to ensure that a thread never attempts to obtain a second lock while holding a first one, which violates the circular wait condition, I also avoided nested locking.
---

### Question 4: Lock Granularity Design Decision 
**Q**: For Task 1 (protecting the three counters), explain your lock design choice:
- Did you use ONE lock for all three counters (coarse-grained) OR separate locks for each counter (fine-grained)?
- Explain WHY you made this choice
- What are the trade-offs between the two approaches?
- Given that the three counters are independent, which approach provides better concurrency and why?

**Your Answer**:
For each of the three counters, I utilized a single coarse-grained lock. Because these variables are updated frequently and using separate locks would add needless complexity and overhead, I took this decision. The performance increase in this simulation would be minimal, even though fine-grained locking improves concurrency because the counters are independent. In exchange, coarse-grained locking ensures complete data consistency and is far safer and simpler to troubleshoot.

---

## Part 3: Synchronization Analysis (1 mark)

Part 3: Synchronization Analysis (1 mark)
Critical Section #1: Counter Variables
Which variables: contextSwitchCount, completedProcessCount, totalWaitingTime.
Why they need protection: Multiple threads perform read-modify-write operations on these shared variables.
Synchronization mechanism used: ReentrantLock.
Code snippet:public static void incrementContextSwitch() {
    lock.lock();
    try { contextSwitchCount++; } finally { lock.unlock(); }
}
```java
// Paste your implementation here
```

**Justification**: 

---

Critical Section #2: Execution Log
What resource: List<String> executionLog.
Why it needs protection: Adding to a shared list is not a thread-safe operation.
Synchronization mechanism used: ReentrantLock.
Code snippet:
public static void logExecution(String message) {
    lock.lock();
    try { executionLog.add(message); } finally { lock.unlock(); }
}
**Justification**: 

---
Critical Section #3: CPU Semaphore
Purpose of semaphore: To simulate a single-core CPU execution.
Number of permits and why: 1 permit, because only one process can occupy the CPU at a time.
Where implemented: Inside the run() method of the Process class.
Code snippet:
SharedResources.cpuSemaphore.acquire();
// ... critical execution logic ...
SharedResources.cpuSemaphore.release();

**Effect on program behavior**: 

---
Part 4: Testing and Verification (2 marks)
Test 1: Consistency Check
What I tested: Running the program 5 times to see if "Average Waiting Time" remains consistent.
Testing procedure:
for i in {1..5}; do java SchedulerSimulationSync; done

**Results**: 


**Why synchronization is necessary**: 
(Explain what race conditions COULD occur without synchronization, even if you didn't observe them. Explain which shared resources need protection and why.)
results: The output was consistent and mathematically correct every time.
**Conclusion**: 

---

Test 2: Exception Testing
What I tested: Absence of ConcurrentModificationException during log access.
Results: Zero crashes or exceptions over multiple long-running tests.

---

### Test 3: Correctness Verification
**What I tested**: Verifying correct final values (total burst time, context switches, etc.)

**Expected values**: 

**Actual values**: 

**Analysis**: 

---

### Test 4: Different Scenarios
**Scenario tested**: [e.g., different time quantum, more processes, etc.]

**Purpose**: 

**Results**: 

**What I learned**: 

---

## Part 5: Reflection and I discovered that the foundation of dependable multi-threaded software is synchronization. Without it, data becomes erratic and difficult-to-replicate "ghost" errors emerge. ReentrantLock and Semaphore are two solutions that offer an organized approach to managing shared resources. I also discovered that a crucial best practice to prevent deadlocks is to put the unlock() function in a finally block.
---

### Real-world applications:

Example 1: Banking systems where multiple transactions must update a single account balance without error.
Example 2: Online ticket booking systems where only one user can buy the last seat at a time.
---

### How I would explain synchronization to others:

[Explain to someone who just finished Assignment 1 - use simple terms and analogies]

---

## Part 6: GitHub Repository Information

**Repository URL**: https://github.com/zeed66/OS-Assignment3-ZEED-ALHODAIB.git

**Number of commits**: 7

**Commit messages**: 
1. Update student ID in SchedulerSimulationSync.java
2. Add synchronization mechanisms using ReentrantLock and Semaphore
3. Add synchronization to shared resource methods using ReentrantLock
   4.Add CPU semaphore acquisition and release in Process run method
5. Refactor run method for better semaphore management
6. Refactor process handling and logging in simulation
7. Refactor synchronization in SchedulerSimulationSync

---

## Summary

**Total time spent on assignment**: 6h
Key takeaways:
Most challenging aspect: Solving the nested bracket errors in the Java code.
What I'm most proud of: Implementing a colorful, readable terminal interface that displays simulation results clearly.
---

**End of Documentation**
