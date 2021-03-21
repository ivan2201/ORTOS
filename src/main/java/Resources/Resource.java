package Resources;

public class Resource {
    public final int id;
    public Semaphore semaphore;
    public final boolean isLocal;

    public Resource(final int id) {
        this.id = id;
        this.semaphore = new Semaphore(this, Semaphore.RECOURSE_RELEASED);
        this.isLocal = false;
    }

    public Resource(final int id, final boolean isLocal) {
        this.id = id;
        this.semaphore = new Semaphore(this, Semaphore.RECOURSE_RELEASED);
        this.isLocal = isLocal;
    }

    public int ownerTaskId() {
        return semaphore.getOwnerTaskId();
    }

    @Override
    public String toString() {
        return String.format("[RESOURCE<ID ресурса: %d, локальный: %b, состояние семафора: ", id, isLocal) + semaphore.toString() + ">]";
    }

    public boolean isFree() {
        return ownerTaskId() == Semaphore.RECOURSE_RELEASED;
    }
}
