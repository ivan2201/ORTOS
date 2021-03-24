package EventGenerators.SimpleTests;

import EventGenerators.EventGenerator;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class LocalResourceTest extends EventGenerator {
    public LocalResourceTest(Consumer<OsEvent> eventConsumer) {
        // Тест на обработку создания локального ресурса:
        // Ждём 1 секунду, пока выполнится начальная задача в ОС.
        // Затем объявляем задачу с приоритетом 5. Ждём 200 мс.
        // Объявляем создание локального ресурса.
        // Выполняем задачу.
        // Освобождаем ресурс
        super(
                Stream.of(
                        1000L,
                        200L
                ).iterator(),
                Stream.of(
                        OsEvent.declareTaskEvent(123, 5),
                        OsEvent.declareResourceEvent(5)
                ).iterator(),
                eventConsumer
        );
    }
}
