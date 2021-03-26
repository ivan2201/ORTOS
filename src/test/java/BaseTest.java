import EventGenerators.EventGenerator;
import OS.OrtOS;
import OS.OsAPI;
import Tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static OS.OrtOS.MAX_PRIORITY;

public class BaseTest {

    protected static final Random RANDOM = new Random(2L);

    protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    public static void simulateOS(final OsAPI os, final EventGenerator eventsGenerator, final long timeout) {
        final Task taskToStart = new Task(0, MAX_PRIORITY, os);
        try {
            os.startOS(taskToStart);
            eventsGenerator.start();
            Thread.sleep(timeout);
            log.debug("Время timeout истекло!");
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } finally {
            os.shutdownOS();
        }
        eventsGenerator.interrupt();
        try {
            eventsGenerator.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static OrtOS createOS() {
        return new OrtOS();
    }

    public static EventGenerator createGenerator(
            final OrtOS os,
            final TestEvent... testEvents
    ) {
        final List<Long> timeouts = new ArrayList<>();
        final List<EventGenerator.OsEvent> events = new ArrayList<>();
        for (final TestEvent testEvent : testEvents) {
            timeouts.add(testEvent.timeout);
            events.add(testEvent.event);
        }
        return new EventGenerator(
                timeouts.iterator(),
                events.iterator(),
                os::interpretEvent
        );
    }

    public static class TestEvent {
        public final long timeout;
        public final EventGenerator.OsEvent event;

        public TestEvent(final long timeout, final EventGenerator.OsEvent event) {
            this.timeout = timeout;
            this.event = event;
        }
    }

}
