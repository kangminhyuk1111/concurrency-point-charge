package core.concurrencypointcharge.point.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "points")
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal balance;

    // 낙관적 락 사용을 위한 Version 필드
    @Version
    private Long version;

    public Point() {
    }

    public Point(final Long userId, final BigDecimal balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getVersion() {
        return version;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(final BigDecimal balance) {
        this.balance = balance;
    }
}
