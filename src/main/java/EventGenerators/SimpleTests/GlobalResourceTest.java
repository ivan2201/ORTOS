package EventGenerators.SimpleTests;

import EventGenerators.EventGenerator;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class GlobalResourceTest extends EventGenerator {
    public GlobalResourceTest(Consumer<EventGenerator.OsEvent> eventConsumer) {
        // Тест на обработку захвата глобального ресурса:
        // Ждём 1 секунду, пока выполнится начальная задача в ОС.
        // Затем объявляем задачу с приоритетом 5. Ждём 200 мс.
        // Объявляем создание локального ресурса. Ждём 400 мс.
        // Захватываем глобальный ресурс 1. Ждём 50 мс.
        // Объявляем задачу 321 с приоритетом 10. Ожидаем вытеснения задачи 123 с приоритетом 5. Ждем 100 мс.
        // Пытаемся захватить глобальный ресурс 1. Он уже захвачен. Возвращаем задачу 123. Ждём 200 мс.
        // Создаем локальный ресурс. Ждём 50 мс.
        // Пытаемся захватить глобальный ресурс 2.
        // Ожидаем завершения задачи 123.
        // Возвращаем задачу 321.
        // Предоставляем ресурс
        // Освобождаем.

        super(
                Stream.of(
                        1000L,
                        200L,
                        400L,
                        50L,
                        100L,
                        200L,
                        50L
                ).iterator(),
                Stream.of(
                        EventGenerator.OsEvent.declareTaskEvent(123, 5),
                        EventGenerator.OsEvent.declareResourceEvent(5),
                        EventGenerator.OsEvent.getGlobalResource(0),
                        EventGenerator.OsEvent.declareTaskEvent(312, 10),
                        EventGenerator.OsEvent.getGlobalResource(0),
                        EventGenerator.OsEvent.declareResourceEvent(321),
                        EventGenerator.OsEvent.getGlobalResource(2)
                ).iterator(),
                eventConsumer
        );
    }
}
