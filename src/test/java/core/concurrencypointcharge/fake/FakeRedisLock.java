package core.concurrencypointcharge.fake;

import core.concurrencypointcharge.point.application.out.DistributedLock;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class FakeRedisLock implements DistributedLock {
    private final ConcurrentHashMap<String, String> lockStore = new ConcurrentHashMap<>();

    @Override
    public boolean tryLock(String key) {
        return lockStore.putIfAbsent(key, "LOCKED") == null;
    }

    @Override
    public void unlock(String key) {
        lockStore.remove(key);
    }
}