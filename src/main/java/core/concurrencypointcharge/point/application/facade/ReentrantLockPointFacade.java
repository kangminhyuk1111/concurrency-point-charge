package core.concurrencypointcharge.point.application.facade;

import core.concurrencypointcharge.point.application.service.PointService;
import core.concurrencypointcharge.point.application.service.ReentrantLockPointService;
import core.concurrencypointcharge.point.domain.Point;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantLock;

@Service("reentrantLockPointService")
public class ReentrantLockPointFacade implements PointService {

    private final ReentrantLockPointService reentrantLockPointService;

    // 전역 락을 Facade에서 관리
    private final ReentrantLock lock = new ReentrantLock();

    public ReentrantLockPointFacade(final ReentrantLockPointService reentrantLockPointService) {
        this.reentrantLockPointService = reentrantLockPointService;
    }

    /**
     * Facade 패턴을 적용하여 ReentrantLock이 트랜잭션 커밋까지 포함하도록 함
     * lock.lock() ~ lock.unlock() 사이에서 트랜잭션이 완전히 커밋된 후에야 다음 스레드가 락을 획득 가능
     */
    @Override
    public Point charge(final Long userId, final Long amount) {
        lock.lock();
        try {
            return reentrantLockPointService.charge(userId, amount);
        } finally {
            lock.unlock();
        }
    }
}
