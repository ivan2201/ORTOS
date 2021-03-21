import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    static final Random RANDOM = new Random(0L);
    static final int MAX_PRIORITY = 10;

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final int GLOBAL_RESOURCES_COUNT = 4;
    private static final List<Resource> globalResources = new ArrayList<>(GLOBAL_RESOURCES_COUNT);

    public static void main(String[] args) {
        final OrtOS ortOs = new OrtOS();

        final Thread eventsGenerator = new EventGenerator((eventType) -> {
            switch (eventType) {
                case declareTask:
                    log.debug("Событие: создание новой задачи.");
                    ortOs.declareTask(
                            RANDOM.nextInt(),
                            RANDOM.nextInt(MAX_PRIORITY)
                    );
                    break;
                case declareResource:
                    ortOs.withCurrentTask((task) -> {
                        log.debug("Событие: создание локального ресурса.");
                        final Resource resource = ortOs.declareResource(RANDOM.nextInt(), true);
                        ortOs.getResource(resource);
                    });
                    break;
                case getRecourse:
                    ortOs.withCurrentTask((task) -> {
                        log.debug("Событие: попытка захвата глобального ресурса.");
                        ortOs.getResource(globalResources.get(RANDOM.nextInt(GLOBAL_RESOURCES_COUNT) ));
                    });
                    break;
                default:
                    throw new IllegalStateException("Событие: генератор вышел из чата.");
            }
        }
        );

        final Task taskToStart = new Task(
                666,
                MAX_PRIORITY,
                ortOs
        );

        try {
            for (int i = 0; i < 4; ++i) {
                globalResources.add(ortOs.declareResource(RANDOM.nextInt(), false));
            }
            ortOs.startOS(taskToStart);
            // Объявляем 4 глобальных ресурса. Задачи могут запрашивать к ним доступ или создавать свои ЛОКАЛЬНЫЕ переменные.
            // Локальные переменные удаляются после вызова releaseResource().
            eventsGenerator.start();
            Thread.sleep(10000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } finally {
            ortOs.shutdownOS();
        }
        eventsGenerator.interrupt();
        try {
            eventsGenerator.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
