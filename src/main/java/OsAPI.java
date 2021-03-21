public interface OsAPI extends UserOsAPI {

    void activateTask(Task task);
    void terminateTask();

    void startOS(Task firstTask);
    void shutdownOS();


}
