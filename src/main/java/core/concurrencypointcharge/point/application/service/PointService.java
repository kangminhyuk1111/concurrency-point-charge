package core.concurrencypointcharge.point.application.service;

import core.concurrencypointcharge.point.domain.Point;

public interface PointService {
    Point charge(Long userId, Long amount);
}
