package core.concurrencypointcharge.point.application.out;

import core.concurrencypointcharge.point.domain.Point;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointRepository {
    Optional<Point> findByUserId(Long userId);
    void deleteByUserId(Long userId);
    void deleteAll();
    Point save(Point point);
    Optional<Point> findByUserIdWithPessimisticLock(Long userId);
}
