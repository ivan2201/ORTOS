import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class OrtOS implements OsAPI {

    final static int MAX_TASK_COUNT = 32;
    final static int MAX_RECOURSE_COUNT = 16;

    private final TaskPriorityQueue taskQueue;
    private final List<Resource> resourceList;
    private final Dispatcher dispatcher;
    private Task currentTask;

    private final Lock currentTaskLock = new ReentrantLock(false);

    private static final Logger log = LoggerFactory.getLogger(OrtOS.class);

    public OrtOS() {
        this.taskQueue = new TaskPriorityQueue(MAX_TASK_COUNT);
        this.resourceList = new CopyOnWriteArrayList<>();
        this.dispatcher = new Dispatcher(this.taskQueue, task -> {
            currentTaskLock.lock();
            try {
                currentTask = task;
            } finally {
                currentTaskLock.unlock();
            }
            if (currentTask != null && currentTask.waitingFor != null) {
                final Resource res = currentTask.waitingFor;
                getResource(res);
                currentTask.waitingFor = null;
                log.debug("Задача " + task + " получила необходимый ресурс " + res);
            }
        });
        this.currentTask = null;
    }

    private Task getActiveTask() {
        final Task snapshotTask;
        currentTaskLock.lock();
        try {
            snapshotTask = currentTask;
        } finally {
            currentTaskLock.unlock();
        }
        return snapshotTask;
    }

    @Override
    public void activateTask(Task task) {

        final Task currentTask = getActiveTask();
        if (currentTask == null) {
            log.debug("Диспетчер простаивает! Ставим на выполнение задачу " + task);
            terminateTask();
            /* дожидаемся, пока задача завершится */
            // dispatcher.isFree();
        } else if (currentTask.priority < task.priority) {
            log.debug("Произошло прерывание! Активируем задачу " + task);
            terminateTask();
            /* дожидаемся, пока задача завершится */
            // dispatcher.isFree();
        }
        taskQueue.add(task);

    }

    public void terminateTask() {
        dispatcher.interrupt();
    }

    public void startOS(final Task firstTask) {
        this.dispatcher.start();
        activateTask(firstTask);
    }

    final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void shutdownOS() {
        shuttingDown.set(true);
        dispatcher.taskQueue.add(Task.POISSON_PILL);
        dispatcher.interrupt();
        try {
            dispatcher.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("This is the end...");
    }

    @Override
    public void getResource(Resource resource) {
        final Task activeTask = getActiveTask();
        if (activeTask == null) {
            throw new IllegalStateException("Кто здесь???");
        }
        final Semaphore semaphore = new Semaphore(resource, activeTask.taskId);
        P(semaphore);
        /*подождали и дождались, что семафор ресурса совпадает с нашим семафором*/
    }

    @Override
    public void releaseResource(Resource resource) {
        V(resource.semaphore);
    }

    @Override
    public void P(final Semaphore newSemaphore) {
        // TODO: можно ли хватать один и тот же ресурс?
        if (newSemaphore.getResource().ownerTaskId()== newSemaphore.getOwnerTaskId()) {
            log.debug("Одна и та же задача хватает один и тот же ресурс.");
            return;
        }
        if (!newSemaphore.getResource().isFree()) {
            /*мы пришли, а семафор перекрыт*/
            // Отдаём управление другой задаче.
            withCurrentTask(task -> {
                task.waitingFor = newSemaphore.getResource();
                log.debug("Задача {} ожидает освобождение ресурса {}", task, newSemaphore.getResource());
                terminateTask();
            });
            return;
        }
        newSemaphore.activate();
        final Task activeTask = getActiveTask();
        if (activeTask.taskId != newSemaphore.getOwnerTaskId()) {
            throw new IllegalStateException("МыЯ (не) захватили ресурс");
        }
        activeTask.mineResources.add(newSemaphore.getResource());
        log.debug("Ресурс {} захвачен задачей {}", newSemaphore.getResource(), activeTask.taskId);
    }

    @Override
    public void V(final Semaphore s) {
        final Task activeTask = getActiveTask();
        if (activeTask != null && s.getOwnerTaskId() != activeTask.taskId) {
            throw new IllegalStateException("Задача пытается освободить ресурс, который ей не принадлежит.");
        }
        s.deactivate();
        if (s.getResource().isLocal) {
            resourceList.remove(s.getResource());
        }
        log.debug("Задача {} отпустила ресурс {}", activeTask, s.getResource());
    }

    /**
     * Осуществляет регистрацию ресурса в системе (назначение подобно объявлению глобальных переменных в
     * языке С). Должен вызываться ДО использования ресурса в коде пользовательского приложения.
     */
    public Resource declareResource(final int resourceId, final boolean isLocal) {
        final boolean present = resourceList
                .stream()
                .anyMatch(resource -> resource.id == resourceId);
        if (present) {
            throw new IllegalStateException("Ресурс с таким ID уже существует!");
        }
        if (resourceList.size() >= MAX_RECOURSE_COUNT) {
            throw new IllegalStateException("Список ресурсов переполнен!");
        }
        final Resource resource = new Resource(resourceId, isLocal);
        log.debug("Создан новый ресурс: " + resource);
        resourceList.add(resource);
        return resource;
    }

    /**
     * Осуществляет регистрацию задачи в системе (назначение подобно объявлению глобальных функций в языке
     * С). Должен вызываться ДО использования задачи в
     * коде пользовательского приложения.
     */
    public Task declareTask(int taskId, int priority) {
        if (shuttingDown.get()) {
            return null;
        }
        final Task task = new Task(taskId, priority, this);
        log.debug("Объявлена новая задача: " + task);
        activateTask(task);
        return task;
    }

    public void withCurrentTask(final Consumer<Task> runnable) {
        currentTaskLock.lock();
        try {
            if (currentTask == null) {
                return;
            }
            runnable.accept(currentTask);
        } finally {
            currentTaskLock.unlock();
        }
    }

//    /**
//     * Переводит задачу из состояния suspended в состояние ready.
//     * Этот сервис используется только в обработчиках прерываний.
//     * Его вызов приводит к планированию задачи
//     * (задача помещается в очередь готовых, но переключения не происходит).
//     * Не допускается одновременное присутствие двух активных копий одной задачи.
//     * @param task -идентификатор задачи, подлежащей активизации
//     */
//    private void ISRActivateTask(@NotNull final Task task) {
//        final Task currentTask = getActiveTask();
//        activateTask(task);
//    }


}
