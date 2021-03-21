import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TaskPayload implements Runnable {

    private static final long EPSILON = 0L;

    private static final Logger log = LoggerFactory.getLogger(TaskPayload.class);
    
    long workingTime;

    final Task task;

    public TaskPayload(final Task task) {
        this.workingTime = (long) (Math.random() * 1500) + 200L;
        this.task = task;
    }

    @Override
    public void run() {
        final long goToBedTime = System.currentTimeMillis();
        long lastWakeupTime = goToBedTime;
        try {
            log.debug("TaskPayload: Начало выполнения задачи" + task +  ". Требуемое время: " + workingTime);
            while (true) {
                Thread.sleep(workingTime);
                final long timeAtAlarm = System.currentTimeMillis();
                if (timeAtAlarm >= lastWakeupTime + workingTime) {
                    break;
                }
                workingTime = workingTime - (timeAtAlarm - lastWakeupTime);
                lastWakeupTime = timeAtAlarm;
            }
        } catch (final InterruptedException e) {
            log.debug("TaskPayload: Выполнение задачи" + task +  ".  прервано.");
        }
        final long awaitTime = System.currentTimeMillis();
        workingTime -= (awaitTime - lastWakeupTime);
        if (done()) {
            log.debug("TaskPayload: Задача" + task +  ".  выполнена успешно.");
        } else {
            log.debug("TaskPayload: Вернёмся к задаче" + task +  ".  позже. Осталось: " + workingTime);
        }
    }

    public boolean done() {
        return workingTime <= EPSILON;
    }


}
