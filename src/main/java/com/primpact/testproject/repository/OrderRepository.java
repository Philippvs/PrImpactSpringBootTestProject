package com.primpact.testproject.repository;

import com.primpact.testproject.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByStatus(String status);

    List<OrderEntity> findByAmountGreaterThan(BigDecimal amount);

    // CHANGE SCENARIO: BUSINESS LOGIC
    @Query("SELECT o FROM OrderEntity o WHERE o.createdAt >= :startDate AND o.createdAt <= :endDate")
    List<OrderEntity> findOrdersInDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT SUM(o.amount) FROM OrderEntity o WHERE o.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") String status);
}
