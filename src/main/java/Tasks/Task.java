package Tasks;

import OS.UserOsAPI;
import Resources.Resource;

import java.util.ArrayList;
import java.util.List;

public class Task implements Comparable<Task> {

    public static final Task POISSON_PILL = new Task(-1, Integer.MIN_VALUE, null, null);

    public final int taskId;
    public final int priority;
    public final TaskPayload payload;
    public final List<Resource> mineResources;
    public Resource waitingFor;

    private final UserOsAPI os;

    public Task(final int taskId, final int priority, final TaskPayload entry, final UserOsAPI os) {
        this.waitingFor = null;
        this.taskId = taskId;
        this.priority = priority;
        this.payload = entry;
        this.mineResources = new ArrayList<>();
        this.os = os;
    }

    public Task(final int taskId, final int priority, final UserOsAPI os) {
        this.waitingFor = null;
        this.taskId = taskId;
        this.priority = priority;
        this.payload = new TaskPayload(this);
        this.mineResources = new ArrayList<>();
        this.os = os;
    }

    public boolean isReady() {
        if (waitingFor == null) {
            return true;
        }
        return waitingFor.isFree();
    }

    @Override
    public int compareTo(final Task task) {
        return -Integer.compare(this.priority, task.priority);
    }

    @Override
    public String toString() {
        return String.format("[TASK<ID задачи: %d, Приоритет: %d>]", taskId, priority);
    }

    public void releaseAllResources() {
        for (final Resource mineResource: mineResources) {
            os.releaseResource(mineResource);
        }
    }
}
