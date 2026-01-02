package core.concurrencypointcharge.cucumber;

import core.concurrencypointcharge.point.application.out.PointRepository;
import core.concurrencypointcharge.point.application.service.PointService;
import core.concurrencypointcharge.point.domain.Point;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointConcurrencyStepDefinitions {

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private ApplicationContext applicationContext;

    private static final Long TEST_USER_ID = 1L;

    @Before
    public void setUp() {
        pointRepository.deleteAll();
    }

    @Given("사용자 {long}번의 초기 잔액은 {long}원이다")
    public void 사용자_초기_잔액_설정(Long userId, Long initialBalance) {
        Point testUser = new Point(userId, BigDecimal.valueOf(initialBalance));
        pointRepository.save(testUser);
    }

    @When("{string}을 사용하여 {int}명이 동시에 {long}원씩 충전한다")
    public void 동시_충전_실행(String serviceName, int threadCount, long amount) throws InterruptedException {
        PointService service = applicationContext.getBean(serviceName, PointService.class);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    service.charge(TEST_USER_ID, amount);
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(30, java.util.concurrent.TimeUnit.SECONDS);
        if (!completed) {
            throw new RuntimeException("타임아웃: 동시 충전이 30초 내에 완료되지 않았습니다.");
        }
        executor.shutdown();
    }

    @Then("사용자 {long}번의 최종 잔액은 {long}원이어야 한다")
    public void 최종_잔액_검증(Long userId, Long expectedBalance) {
        Point result = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

        assertEquals(
                expectedBalance,
                result.getBalance().longValue(),
                String.format("최종 잔액 불일치. 기대: %d원, 실제: %d원", expectedBalance, result.getBalance().longValue())
        );
    }
}
