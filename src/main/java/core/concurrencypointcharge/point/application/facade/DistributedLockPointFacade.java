package core.concurrencypointcharge.point.application.facade;

import core.concurrencypointcharge.point.application.out.DistributedLock;
import core.concurrencypointcharge.point.application.service.DistributedLockPointService;
import core.concurrencypointcharge.point.application.service.PointService;
import core.concurrencypointcharge.point.domain.Point;
import org.springframework.stereotype.Service;

@Service("distributedLockPointService")
public class DistributedLockPointFacade implements PointService {

    private final DistributedLockPointService distributedLockPointService;
    private final DistributedLock distributedLock;

    private static final int MAX_RETRY_COUNT = 100;
    private static final int LOCK_WAIT_TIME_MS = 50;

    public DistributedLockPointFacade(
            final DistributedLockPointService distributedLockPointService,
            final DistributedLock distributedLock
    ) {
        this.distributedLockPointService = distributedLockPointService;
        this.distributedLock = distributedLock;
    }

    /**
     * 분산 락을 이용한 동시성 제어
     * Redis의 SETNX(Set if Not eXists) 방식을 시뮬레이션
     * 락 획득 실패 시 대기 후 재시도
     */
    @Override
    public Point charge(final Long userId, final Long amount) {
        final String lockKey = "point:lock:" + userId;
        int retryCount = 0;

        // 락 획득 시도
        while (retryCount < MAX_RETRY_COUNT) {
            if (distributedLock.tryLock(lockKey)) {
                try {
                    // 락 획득 성공 - 비즈니스 로직 실행
                    return distributedLockPointService.charge(userId, amount);
                } finally {
                    // 락 해제 (반드시 실행되도록 finally 블록에 위치)
                    distributedLock.unlock(lockKey);
                }
            }

            // 락 획득 실패 - 대기 후 재시도
            retryCount++;
            if (retryCount >= MAX_RETRY_COUNT) {
                throw new RuntimeException("최대 재시도 횟수를 초과했습니다. 락을 획득할 수 없습니다.");
            }

            try {
                Thread.sleep(LOCK_WAIT_TIME_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("락 대기 중 인터럽트 발생", e);
            }
        }

        throw new RuntimeException("충전 처리에 실패했습니다.");
    }
}
