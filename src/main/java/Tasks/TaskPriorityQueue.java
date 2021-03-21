package Tasks;

import Tasks.Task;

import java.util.concurrent.PriorityBlockingQueue;

public class TaskPriorityQueue {

    private final PriorityBlockingQueue<Task> taskQueue;
    private final int maxTaskCount;

    public TaskPriorityQueue(final int maxTaskCount) {
        this.maxTaskCount = maxTaskCount;
        taskQueue = new PriorityBlockingQueue<>(maxTaskCount);
    }

    public Task take() throws InterruptedException {
        return taskQueue.take();
    }

    public void add(final Task task) {
        if (size() > maxTaskCount) {
            throw new IllegalStateException("О нет! Мы достигли порога очереди!");
        }
        final boolean present = taskQueue
                .stream()
                .anyMatch(tsk -> tsk.taskId == task.taskId);
        if (present) {
            throw new IllegalStateException("Задача с таким ID уже существует!");
        }
        taskQueue.add(task);
    }

    public int size() {
        return taskQueue.size();
    }

}
