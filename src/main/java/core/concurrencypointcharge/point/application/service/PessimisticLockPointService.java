package core.concurrencypointcharge.point.application.service;

import core.concurrencypointcharge.point.application.out.PointRepository;
import core.concurrencypointcharge.point.domain.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service("pessimisticLockPointService")
public class PessimisticLockPointService implements PointService {

    private final PointRepository pointRepository;

    public PessimisticLockPointService(final PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    @Transactional
    public Point charge(Long userId, Long amount) {
        final Point point = pointRepository.findByUserIdWithPessimisticLock(userId)
                .orElseThrow(() -> new RuntimeException("포인트 계좌가 존재하지 않습니다."));

        BigDecimal currentBalance = point.getBalance();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        point.setBalance(currentBalance.add(BigDecimal.valueOf(amount)));

        return pointRepository.save(point);
    }
}
