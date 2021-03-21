import java.util.concurrent.atomic.AtomicInteger;

public class Semaphore {

    static final int RECOURSE_RELEASED = -1;

    private final Resource resource;
    private final AtomicInteger ownerTaskId;

    public Semaphore(Resource resource, int ownerTaskId) {
        this.resource = resource;
        this.ownerTaskId = new AtomicInteger(ownerTaskId);
    }

    public int getOwnerTaskId() {
        return ownerTaskId.get();
    }

    public Resource getResource() {
        return resource;
    }

    public void activate() {
        resource.semaphore = this;
    }

    public void deactivate() {
        ownerTaskId.set(Semaphore.RECOURSE_RELEASED);
    }

    @Override
    public String toString() {
        return String.format(
                "[SEMAPHORE<ID ресурса: %d, ID задачи: %d, открыт: %b>]",
                resource == null ? null : resource.id, ownerTaskId.get(), ownerTaskId.get() == RECOURSE_RELEASED
        );
    }
}
