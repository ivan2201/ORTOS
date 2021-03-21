package EventGenerators;

import Resources.Resource;

import java.util.Iterator;
import java.util.function.Consumer;

public class EventGenerator extends Thread {

    public static class OsEvent {
        public final EventType eventType;
        public final Integer taskId;
        public final Integer taskPriority;
        public final Integer resourceId;
        public final Integer globalResourceIndex;

        private OsEvent(EventType eventType, Integer taskId, Integer taskPriority, Integer resourceId, Integer globalResourceIndex) {
            this.eventType = eventType;
            this.taskId = taskId;
            this.taskPriority = taskPriority;
            this.resourceId = resourceId;
            this.globalResourceIndex = globalResourceIndex;
        }

        public static OsEvent declareTaskEvent(final int taskId, final int taskPriority) {
            return new OsEvent(EventType.declareTask, taskId, taskPriority, null, null);
        }

        public static OsEvent declareResourceEvent(final int resourceId) {
            return new OsEvent(EventType.declareResource, null, null, resourceId, null);
        }

        public static OsEvent getGlobalResource(final int globalResourceIndex) {
            return new OsEvent(EventType.getRecourse, null, null, null, globalResourceIndex);
        }
    }

    public enum EventType {
        // добавь задачу в очередь
        declareTask,
        // создай ресурс внутри текущей задачи
        declareResource,
        // выдай рандомный ресурс текущей задаче (не может быть выдана локальная переменная!)
        getRecourse,
    }

    final Consumer<OsEvent> eventConsumer;
    final Iterator<Long> sleepGenerator;
    final Iterator<OsEvent> typeGenerator;

    public EventGenerator(
            final Iterator<Long> sleepIterator,
            final Iterator<OsEvent> eventIterator,
            final Consumer<OsEvent> eventConsumer
    ) {
        this.sleepGenerator = sleepIterator;
        this.typeGenerator = eventIterator;
        this.eventConsumer = eventConsumer;
        this.setName("OS");
    }

    @Override
    public void run() {
        while (true) {
            if (isInterrupted()) {
                break;
            }
            try {
                if (!sleepGenerator.hasNext()) {
                    return;
                }
                sleep(sleepGenerator.next());
            } catch (final InterruptedException e) {
                break;
            }
            if (!typeGenerator.hasNext()) {
                return;
            }
            final OsEvent randomEvent = typeGenerator.next();
            eventConsumer.accept(randomEvent);
        }
    }
}
