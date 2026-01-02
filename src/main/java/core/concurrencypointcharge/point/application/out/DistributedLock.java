package core.concurrencypointcharge.point.application.out;

public interface DistributedLock {
    boolean tryLock(String key);
    void unlock(String key);
}
