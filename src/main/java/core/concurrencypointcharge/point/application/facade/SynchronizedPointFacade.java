package core.concurrencypointcharge.point.application.facade;

import core.concurrencypointcharge.point.application.service.PointService;
import core.concurrencypointcharge.point.application.service.SynchronizedPointService;
import core.concurrencypointcharge.point.domain.Point;
import org.springframework.stereotype.Service;

@Service("synchronizedPointService")
public class SynchronizedPointFacade implements PointService {

    private final SynchronizedPointService synchronizedPointService;

    public SynchronizedPointFacade(final SynchronizedPointService synchronizedPointService) {
        this.synchronizedPointService = synchronizedPointService;
    }

    /**
     * Facade 패턴을 적용하여 synchronized 블록이 트랜잭션 커밋까지 포함하도록 함
     * synchronized 블록 내에서 트랜잭션이 완전히 커밋된 후에야 다음 스레드가 진입 가능
     */
    @Override
    public synchronized Point charge(final Long userId, final Long amount) {
        return synchronizedPointService.charge(userId, amount);
    }
}
