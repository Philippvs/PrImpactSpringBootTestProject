package com.primpact.testproject.client;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

// CHANGE SCENARIO: BUSINESS LOGIC
@Component
public class FakePaymentClient {

    private static final BigDecimal PAYMENT_LIMIT = BigDecimal.valueOf(10000);

    public boolean processPayment(Long orderId, BigDecimal amount) {
        simulateNetworkDelay();
        
        if (amount.compareTo(PAYMENT_LIMIT) > 0) {
            return false;
        }
        
        return Math.random() > 0.1;
    }

    public PaymentStatus checkPaymentStatus(String transactionId) {
        simulateNetworkDelay();
        return PaymentStatus.COMPLETED;
    }

    public boolean refundPayment(String transactionId) {
        simulateNetworkDelay();
        return true;
    }

    private void simulateNetworkDelay() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, REFUNDED
    }
}
