package core.concurrencypointcharge.point;

import core.concurrencypointcharge.point.application.out.PointRepository;
import core.concurrencypointcharge.point.application.service.PointService;
import core.concurrencypointcharge.point.domain.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DisplayName("포인트 충전 동시성 테스트")
public class ConcurrencyPointServiceTest {

    private static final Long TEST_USER_ID = 1L;
    private static final long INITIAL_BALANCE = 1000L;
    private static final long CHARGE_AMOUNT = 100L;
    private static final int CONCURRENT_THREADS = 10;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private ApplicationContext ac;

    @BeforeEach
    public void setUp() {
        pointRepository.deleteAll();

        Point testUser = new Point(TEST_USER_ID, BigDecimal.valueOf(INITIAL_BALANCE));
        pointRepository.save(testUser);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "synchronizedPointService",
            "reentrantLockPointService",
            "pessimisticLockPointService",
            "optimisticLockPointService",
            "distributedLockPointService",
    })
    void 동시성_테스트_10명_동시충전시_성공(final String serviceName) throws InterruptedException {
        PointService service = ac.getBean(serviceName, PointService.class);

        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_THREADS);

        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            executor.submit(() -> {
                try {
                    service.charge(TEST_USER_ID, CHARGE_AMOUNT);
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(30, java.util.concurrent.TimeUnit.SECONDS);
        if (!completed) {
            throw new RuntimeException("Timeout");
        }
        executor.shutdown();

        Point result = pointRepository.findByUserId(TEST_USER_ID)
                .orElseThrow(() -> new RuntimeException("포인트 계좌를 찾을 수 없음"));

        Long expectedBalance = INITIAL_BALANCE + (CONCURRENT_THREADS * CHARGE_AMOUNT);
        assertEquals(
                expectedBalance,
                result.getBalance().longValue(),
                String.format(
                        "[%s] 최종 잔액 불일치. 기대: %d, 실제: %d",
                        serviceName,
                        expectedBalance,
                        result.getBalance().longValue()
                )
        );
    }
}
