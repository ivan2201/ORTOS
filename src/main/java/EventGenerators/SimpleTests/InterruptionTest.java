package EventGenerators.SimpleTests;

import EventGenerators.EventGenerator;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class InterruptionTest extends EventGenerator {
    public InterruptionTest(Consumer<OsEvent> eventConsumer) {
        // Тест на обработку прерываний:
        // Ждём 1 секунду, пока выполнится начальная задача в ОС.
        // Затем объявляем задачу с приоритетом 5. Ждём 200 мс.
        // Объявляем задачу с высшим приоритетом и ожидаем, что она вытеснит предыдущую.
        super(
                Stream.of(
                        1000L,
                        200L
                ).iterator(),
                Stream.of(
                        OsEvent.declareTaskEvent(123, 5),
                        OsEvent.declareTaskEvent(1234, 10)
                ).iterator(),
                eventConsumer
        );
    }
}
