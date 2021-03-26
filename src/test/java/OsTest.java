import EventGenerators.EventGenerator;
import OS.OrtOS;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static OS.OrtOS.MAX_TASK_COUNT;

public class OsTest extends BaseTest {

    private final String noExceptionString = "Поток операционной системы не должен выбрасывать исключения во время работы";

    // Тест на обработку прерываний
    // Ждём 2 секунды, пока выполнится начальная задача в ОС.
    // Затем объявляем задачу с приоритетом 5. Ждём 200 мс.
    // Объявляем задачу с высшим приоритетом и ожидаем, что она вытеснит предыдущую.
    @Test
    public void interruptTest() {
        final OrtOS os = createOS();
        final int tasksCount = 2;
        final EventGenerator generator = createGenerator(
                os,
                new TestEvent(2000L, EventGenerator.OsEvent.declareTaskEvent(123, 5)),
                new TestEvent(200L, EventGenerator.OsEvent.declareTaskEvent(1234, 10))
        );
        Assertions.assertDoesNotThrow(() -> simulateOS(os, generator, 5000L), noExceptionString);
        os.printSystemInfo();
        Assertions.assertEquals(os.info.getInterruptionsCount(), 1, "Количество прерываний должно быть равно 1");
        Assertions.assertEquals(os.info.getTasksDoneCount(), tasksCount + 1, "Выполненных задач должно быть на одну больше, чем было задано (так как старт системы - тоже задача)");
        Assertions.assertTrue(os.info.getDispatcherFinishedCorrectly(), "Диспетчер должен завершиться без ошибок");
        Assertions.assertTrue(os.info.getOsFinishedCorrectly(), "ОС должна завершиться без ошибок");
    }

    // Тест на захват глобального ресурса
    @Test
    public void globalResourceTest() {
        final OrtOS os = createOS();
        final int tasksCount = 3;
        final EventGenerator generator = createGenerator(
                os,
                // Ждём 2 секунды, пока выполнится начальная задача в ОС. Затем объявляем задачу с приоритетом 5.
                new TestEvent(2000L, EventGenerator.OsEvent.declareTaskEvent(1, 5)),
                // Ждём 10 мс. Захватываем глобальный ресурс 0.
                new TestEvent(10L, EventGenerator.OsEvent.getGlobalResource(0)),
                // Ждём 100 мс. Объявляем задачу с приоритетом 10. Ожидаем вытеснения задачи с приоритетом 5.
                new TestEvent(100L, EventGenerator.OsEvent.declareTaskEvent(2, 10)),
                // Ждем 10 мс. Пытаемся захватить глобальный ресурс 0. Он уже захвачен. Возвращаем задачу с приоритетом 5.
                new TestEvent(10L, EventGenerator.OsEvent.getGlobalResource(0)),
                // Ждём 3000 мс, пока задачи закончатся. Объявляем задачу с приоритетом 0. Прерывания быть не должно - выполнится после всех.
                new TestEvent(3000L, EventGenerator.OsEvent.declareTaskEvent(3, 0)),
                // Ждём 10 мс и захватываем глобальный ресурс, который уже должен быть свободен.
                new TestEvent(10L, EventGenerator.OsEvent.getGlobalResource(0))
        );
        Assertions.assertDoesNotThrow(() -> simulateOS(os, generator, 7000L), noExceptionString);
        os.printSystemInfo();
        Assertions.assertEquals(os.info.getInterruptionsCount(), 1, "Количество прерываний должно быть равно 1");
        Assertions.assertEquals(os.info.getTasksDoneCount(), tasksCount + 1, "Выполненных задач должно быть на одну больше, чем было задано (так как старт системы - тоже задача)");
        Assertions.assertEquals(os.info.getWaitingForResourceTasksCount(), 1, "Количество задач в ожидании ресурсов должно быть равно 1");
        Assertions.assertEquals(os.info.getGotWaitingForResourceTasksCount(), 1, "Количество задач, которые получили ожидаемый ресурс, должно быть равно 1");
        Assertions.assertTrue(os.info.getDispatcherFinishedCorrectly(), "Диспетчер должен завершиться без ошибок");
        Assertions.assertTrue(os.info.getOsFinishedCorrectly(), "ОС должна завершиться без ошибок");
    }

    // Тест на создание локальных ресурсов
    // Ждём 2 секунды, пока выполнится начальная задача в ОС.
    // Затем объявляем MAX_TASKS_COUNT задач, в каждой из которых создаём локальный ресурс.
    // Поскольку после выполнения каждой задачи локальный ресурс уничтожается, то переполнения не произойдёт.
    @Test
    public void localResourceTest() {
        final OrtOS os = createOS();
        final TestEvent[] testEvents = new TestEvent[MAX_TASK_COUNT * 2];
        for (int i = 0; i < MAX_TASK_COUNT * 2; i += 2) {
            testEvents[i] = new TestEvent(
                    1000L,
                    EventGenerator.OsEvent.declareTaskEvent(RANDOM.nextInt(), 0)
            );
            testEvents[i + 1] = new TestEvent(
                    10L,
                    EventGenerator.OsEvent.declareResourceEvent(RANDOM.nextInt())
            );
        }
        final EventGenerator generator = createGenerator(os, testEvents);
        Assertions.assertDoesNotThrow(() -> simulateOS(os, generator, MAX_TASK_COUNT * 1100), noExceptionString);
        os.printSystemInfo();
        Assertions.assertEquals(os.info.getLocalResourcesDeclared(), MAX_TASK_COUNT, "Количество объявленных локальных ресурсов должно быть равно " + MAX_TASK_COUNT);
        Assertions.assertTrue(os.info.getDispatcherFinishedCorrectly(), "Диспетчер должен завершиться без ошибок");
        Assertions.assertTrue(os.info.getOsFinishedCorrectly(), "ОС должна завершиться без ошибок");
    }

    // Тест на deadlock ситуацию
    @Test
    public void deadlockTest() {
        final OrtOS os = createOS();
        final EventGenerator generator = createGenerator(
                os,
                // Ждём 2 секунды, пока выполнится начальная задача в ОС. Затем объявляем задачу с приоритетом 5.
                new TestEvent(2000L, EventGenerator.OsEvent.declareTaskEvent(1, 5)),
                // Ждём 20 мс. Захватываем глобальный ресурс 0.
                new TestEvent(20L, EventGenerator.OsEvent.getGlobalResource(0)),
                // Ждём 100 мс. Объявляем задачу с высшим приоритетом и ожидаем, что она вытеснит предыдущую.
                new TestEvent(100L, EventGenerator.OsEvent.declareTaskEvent(2, 10)),
                // Ждём 20 мс. Захватываем ресурс 1.
                new TestEvent(20L, EventGenerator.OsEvent.getGlobalResource(1)),
                // Ждём 20 мс. Пытаемся захватить ресурс 0 - и засыпаем.
                new TestEvent(20L, EventGenerator.OsEvent.getGlobalResource(0)),
                // Ждём 100 мс. Проснувшаяся задача с приоритетом 5 пытается захватить ресурс 1 и тоже засыпает.
                new TestEvent(100L, EventGenerator.OsEvent.getGlobalResource(1))
        );
        Assertions.assertDoesNotThrow(() -> simulateOS(os, generator, 5000L), noExceptionString);
        os.printSystemInfo();
        Assertions.assertEquals(os.info.getInterruptionsCount(), 1, "Количество прерываний должно быть равно 1");
        Assertions.assertEquals(os.info.getTasksDoneCount(), 1, "Только задача старта системы должна быть выполнена.");
        Assertions.assertTrue(os.info.hasDeadlocks(), "Должно быть состояние дедлока");
        Assertions.assertTrue(os.info.getDispatcherFinishedCorrectly(), "Диспетчер должен завершиться без ошибок");
        Assertions.assertTrue(os.info.getOsFinishedCorrectly(), "ОС должна завершиться без ошибок");
    }
}
