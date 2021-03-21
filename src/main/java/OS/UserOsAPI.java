package OS;

import Resources.Resource;
import Resources.Semaphore;

public interface UserOsAPI {
    void getResource(Resource resource);
    void releaseResource(Resource resource);
    void P(Semaphore S);
    void V(Semaphore S);
}
