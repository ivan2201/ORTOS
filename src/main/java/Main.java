import EventGenerators.InfinityEventGenerator;
import OS.OrtOS;
import Tasks.Task;

import static OS.OrtOS.MAX_PRIORITY;

public class Main {

    public static void main(String[] args) {
        final OrtOS ortOs = new OrtOS();
        final Thread eventsGenerator = new InfinityEventGenerator(ortOs::interpretEvent);
        final Task taskToStart = new Task(0, MAX_PRIORITY, ortOs);
        try {
            ortOs.startOS(taskToStart);
            eventsGenerator.start();
            Thread.sleep(5000);
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
        ortOs.printSystemInfo();
    }
}
