package core.concurrencypointcharge.point.application.facade;

import core.concurrencypointcharge.point.application.service.OptimisticLockPointService;
import core.concurrencypointcharge.point.application.service.PointService;
import core.concurrencypointcharge.point.domain.Point;
import jakarta.persistence.OptimisticLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service("optimisticLockPointService")
public class OptimisticLockPointFacade implements PointService {

    private final OptimisticLockPointService optimisticLockPointService;
    private static final int MAX_RETRY_COUNT = 50;

    public OptimisticLockPointFacade(final OptimisticLockPointService optimisticLockPointService) {
        this.optimisticLockPointService = optimisticLockPointService;
    }

    /**
     * 낙관적 락 실패 시 재시도 로직
     * @Version 필드를 이용한 동시성 제어
     * OptimisticLockException 발생 시 최대 MAX_RETRY_COUNT까지 재시도
     */
    @Override
    public Point charge(final Long userId, final Long amount) {
        int retryCount = 0;

        while (retryCount < MAX_RETRY_COUNT) {
            try {
                return optimisticLockPointService.charge(userId, amount);
            } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
                retryCount++;
                if (retryCount >= MAX_RETRY_COUNT) {
                    throw new RuntimeException("최대 재시도 횟수를 초과했습니다.", e);
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("재시도 중 인터럽트 발생", ie);
                }
            }
        }

        throw new RuntimeException("충전 처리에 실패했습니다.");
    }
}
