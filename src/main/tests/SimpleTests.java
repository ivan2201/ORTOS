import EventGenerators.SimpleTests.GlobalResourceTest;
import OS.OrtOS;
import Tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static OS.OrtOS.MAX_PRIORITY;

public class SimpleTests {

    private static final Logger log = LoggerFactory.getLogger(SimpleTests.class);

    public static void main(String[] args) {
        final OrtOS ortOs = new OrtOS();

//        final Thread eventsGenerator = new EventGenerators.SimpleTests.InterruptionTest(ortOs::interpretEvent);
//        final Thread eventsGenerator = new EventGenerators.SimpleTests.LocalResourceTest(ortOs::interpretEvent);
        final Thread eventsGenerator = new GlobalResourceTest(ortOs::interpretEvent);
        final Task taskToStart = new Task(0, MAX_PRIORITY, ortOs);
        try {
            ortOs.startOS(taskToStart);
            eventsGenerator.start();
            //test 1 and 2
//            Thread.sleep(5000);
            //test3
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
