package core.concurrencypointcharge.point.application.service;

import core.concurrencypointcharge.point.application.out.PointRepository;
import core.concurrencypointcharge.point.domain.Point;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component("synchronizedPointServiceImpl")
public class SynchronizedPointService {

    private final PointRepository pointRepository;

    public SynchronizedPointService(final PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    @Transactional
    public Point charge(final Long userId, final Long amount) {
        final Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("포인트 계좌가 존재하지 않습니다."));
        final BigDecimal currentBalance = point.getBalance();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        point.setBalance(currentBalance.add(BigDecimal.valueOf(amount)));
        return pointRepository.save(point);
    }
}
