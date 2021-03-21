package EventGenerators;

import OS.OrtOS;

import java.util.Iterator;
import java.util.Random;
import java.util.function.Consumer;

public class InfinityEventGenerator extends EventGenerator {

    private static final Random RANDOM = new Random(1L);

    public InfinityEventGenerator(final Consumer<OsEvent> eventConsumer) {
        super(
                new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return true;
                    }

                    @Override
                    public Long next() {
                        return RANDOM.nextInt(100) + 100L;
                    }
                },
                new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return true;
                    }

                    @Override
                    public OsEvent next() {
                        final EventType eventType = EventType.values()[RANDOM.nextInt(EventGenerator.EventType.values().length)];
                        switch (eventType) {
                            case declareTask:
                                return OsEvent.declareTaskEvent(RANDOM.nextInt(), RANDOM.nextInt(MAX_PRIORITY));
                            case declareResource:
                                return OsEvent.declareResourceEvent(RANDOM.nextInt());
                            case getRecourse:
                                return OsEvent.getGlobalResource(RANDOM.nextInt(OrtOS.GLOBAL_RESOURCES_COUNT));
                            default:
                                throw new IllegalStateException("Неизвестный тип события: " + eventType);
                        }
                    }
                },
                eventConsumer
        );
    }
}
