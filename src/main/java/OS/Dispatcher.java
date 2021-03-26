package OS;

import Tasks.Task;
import Tasks.TaskPriorityQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Dispatcher extends Thread {

    public final TaskPriorityQueue taskQueue;
    public final Consumer<Task> currentTaskCallback;

    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);

    private final AtomicBoolean isFree = new AtomicBoolean(true);

    public Dispatcher(final TaskPriorityQueue taskQueue, final Consumer<Task> currentTaskCallback) {
        this.setName("OS.Dispatcher");
        this.taskQueue = taskQueue;
        this.currentTaskCallback = currentTaskCallback;
        log.debug("Диспетчер готов к работе!");
    }

    @Override
    public void run() {
        while (true) {
            Task task = null;
            try {
                final List<Task> waitingTasks = new ArrayList<>();
                while (true) {
                    task = taskQueue.take();
                    if (task.isReady()) {
                        break;
                    }
                    waitingTasks.add(task);
                }
                for (final Task waitingTask : waitingTasks) {
                    taskQueue.add(waitingTask);
                }
                isFree.set(false);
                if (task.payload == null) {
                    // ммм, сладкая пилюля с ядом...
                    if (taskQueue.size() > 0) {
                        log.debug("В диспетчере остались заблокированные задачи ({} штук)", taskQueue.size());
                    }
                    log.debug("Диспетчер завершает свою работу.");
                    return;
                }
                log.debug("Диспетчер взял задачу " + task);
                currentTaskCallback.accept(task);
                task.payload.run();
                isFree.set(true);
                // isFree = true => Диспетчер нельзя прервать, когда он свободен.
                if (!task.payload.done()) {
                    log.debug("Диспетчер вернул задачу  " + task + " в очередь");
                    taskQueue.add(task);
                } else {
                    task.releaseAllResources();
                    log.debug("Диспетчер отпустил задачу " + task);
                }
                // закончили работу
                currentTaskCallback.accept(null);
            } catch (final InterruptedException | RuntimeException e) {
                isFree.set(true);
                // нас прервали!
                if (task != null && !task.payload.done()) {
                    log.debug("Диспетчер вернул задачу  " + task + " в очередь");
                    taskQueue.add(task);
                }
                log.debug("Исполнение задачи " + task + " прервано. Диспетчер переходит к следующей.");
            }
        }
    }

    @Override
    public void interrupt() {
        if (isFree.get()) {
            log.debug("is free");
            return;
        }
        throw new RuntimeException();
    }
}
