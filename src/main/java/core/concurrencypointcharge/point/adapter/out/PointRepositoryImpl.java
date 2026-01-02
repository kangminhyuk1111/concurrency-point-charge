package core.concurrencypointcharge.point.adapter.out;

import core.concurrencypointcharge.point.application.out.PointRepository;
import core.concurrencypointcharge.point.domain.Point;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PointRepositoryImpl implements PointRepository {

    private final JpaPointRepository jpaPointRepository;

    public PointRepositoryImpl(final JpaPointRepository jpaPointRepository) {
        this.jpaPointRepository = jpaPointRepository;
    }

    @Override
    public Optional<Point> findByUserId(final Long userId) {
        return jpaPointRepository.findByUserId(userId);
    }

    @Override
    public void deleteByUserId(final Long userId) {
        jpaPointRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteAll() {
        jpaPointRepository.deleteAll();
    }

    @Override
    public Point save(final Point point) {
        return jpaPointRepository.save(point);
    }

    @Override
    public Optional<Point> findByUserIdWithPessimisticLock(final Long userId) {
        return jpaPointRepository.findByUserIdWithPessimisticLock(userId);
    }
}
