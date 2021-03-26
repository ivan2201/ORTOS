package OS;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class OsInfo {
    private final AtomicInteger tasksDoneCount;
    private final AtomicInteger tasksTookCount;
    private final AtomicInteger waitingForResourceTasksCount;
    private final AtomicInteger gotWaitingForResourceTasksCount;
    private final AtomicInteger maxTaskPull;
    private final AtomicInteger maxRecoursesPull;
    private final AtomicInteger interruptionsCount;
    private final AtomicBoolean dispatcherFinishedCorrectly;
    private final AtomicBoolean osFinishedCorrectly;
    private final AtomicInteger localResourcesDeclared;

    public OsInfo() {
        tasksDoneCount = new AtomicInteger(0);
        tasksTookCount = new AtomicInteger(1);

        localResourcesDeclared = new AtomicInteger(0);

        maxRecoursesPull = new AtomicInteger(0);
        maxTaskPull = new AtomicInteger(0);

        interruptionsCount = new AtomicInteger(0);

        dispatcherFinishedCorrectly = new AtomicBoolean(false);
        osFinishedCorrectly = new AtomicBoolean(false);

        waitingForResourceTasksCount = new AtomicInteger(0);
        gotWaitingForResourceTasksCount = new AtomicInteger(0);
    }

    public void incrementLocalResourcesDeclared() {
        localResourcesDeclared.incrementAndGet();
    }

    public void incrementWaitingForResourceTasksCount() {
        waitingForResourceTasksCount.incrementAndGet();
    }

    public void incrementGotWaitingForResourceTasksCount() {
        gotWaitingForResourceTasksCount.incrementAndGet();
    }
    
    public void incrementTasksDoneCount() {
        tasksDoneCount.incrementAndGet();
    }

    public void incrementTasksTookCount() {
        tasksTookCount.incrementAndGet();
    }

    synchronized public void updateMaxTaskPull(final int size) {
        if (Math.max(maxTaskPull.get(), size) == size) {
            maxTaskPull.set(size);
        }
    }

    synchronized public void updateMaxRecoursesPull(final int size) {
        if (Math.max(maxRecoursesPull.get(), size) == size) {
            maxRecoursesPull.set(size);
        }
    }

    public void incrementInterruptionsCount() {
        interruptionsCount.incrementAndGet();
    }

    public void setDispatcherFinishedCorrectly() {
        dispatcherFinishedCorrectly.set(true);
    }

    public void setOsFinishedCorrectly() {
        osFinishedCorrectly.set(true);
    }
    
    public int getTasksDoneCount() {
        return tasksDoneCount.get();
    }

    public int getTasksTookCount() {
        return tasksTookCount.get();
    }

    public int getLocalResourcesDeclared() {
        return localResourcesDeclared.get();
    }

    public int getMaxTaskPull() {
        return maxTaskPull.get();
    }

    public int getMaxRecoursesPull() {
        return maxRecoursesPull.get();
    }

    public int getInterruptionsCount() {
        return interruptionsCount.get();
    }

    public boolean getDispatcherFinishedCorrectly() {
        return dispatcherFinishedCorrectly.get();
    }

    public boolean getOsFinishedCorrectly() {
        return osFinishedCorrectly.get();
    }

    public boolean hasDeadlocks() {
        return gotWaitingForResourceTasksCount.get() != waitingForResourceTasksCount.get();
    }

    public int getWaitingForResourceTasksCount() {
        return waitingForResourceTasksCount.get();
    }

    public int getGotWaitingForResourceTasksCount() {
        return gotWaitingForResourceTasksCount.get();
    }
}
