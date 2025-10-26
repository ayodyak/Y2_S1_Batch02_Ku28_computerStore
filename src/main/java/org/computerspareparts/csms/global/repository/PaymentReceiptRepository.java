package org.computerspareparts.csms.global.repository;

import org.computerspareparts.csms.global.entity.PaymentReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentReceiptRepository extends JpaRepository<PaymentReceipt, Long> {

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentReceipt p WHERE p.order.orderId = :orderId")
    BigDecimal sumAmountByOrderId(@Param("orderId") Long orderId);

    // Find all receipts paid between two datetimes (inclusive)
    List<PaymentReceipt> findByPaidAtBetween(LocalDateTime start, LocalDateTime end);
}
