import java.util.Random;
import java.util.function.Consumer;

public class EventGenerator extends Thread {

    public enum EventType {
        // добавь задачу в очередь
        declareTask,
        // создай ресурс внутри текущей задачи
        declareResource,
        // выдай рандомный ресурс текущей задаче (не может быть выдана локальная переменная!)
        getRecourse,
    }

    private static final Random RANDOM = new Random(1L);

    final Consumer<EventType>  eventConsumer;

    public EventGenerator(Consumer<EventType> eventConsumer) {
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
                sleep(RANDOM.nextInt(100) + 100L);
            } catch (final InterruptedException e) {
                break;
            }
            final EventType randomEvent = EventType.values()[RANDOM.nextInt(EventType.values().length)];
            eventConsumer.accept(randomEvent);
        }
    }
}
