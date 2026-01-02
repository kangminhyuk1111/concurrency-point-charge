# language: ko
기능: 포인트 충전 동시성 제어
  사용자들이 동시에 포인트를 충전할 때 데이터 정합성이 보장되어야 한다

  배경:
    조건 사용자 1번의 초기 잔액은 1000원이다

  시나리오 개요: 10명이 동시에 100원씩 충전할 때 최종 잔액이 정확해야 한다
    만약 "<동시성_제어_방식>"을 사용하여 10명이 동시에 100원씩 충전한다
    그러면 사용자 1번의 최종 잔액은 2000원이어야 한다

    예:
      | 동시성_제어_방식             |
      | synchronizedPointService     |
      | reentrantLockPointService    |
      | pessimisticLockPointService  |
      | optimisticLockPointService   |
      | distributedLockPointService  |
