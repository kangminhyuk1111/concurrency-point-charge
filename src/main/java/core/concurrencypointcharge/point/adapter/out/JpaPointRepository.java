package core.concurrencypointcharge.point.adapter.out;

import core.concurrencypointcharge.point.domain.Point;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaPointRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByUserId(Long userId);

    @Modifying
    void deleteByUserId(Long userId);

    // 비관 락 조회 (SELECT FOR UPDATE)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Point p WHERE p.userId = :userId")
    Optional<Point> findByUserIdWithPessimisticLock(@Param("userId") Long userId);
}
