package com.primpact.testproject.service;

import com.primpact.testproject.client.FakePaymentClient;
import com.primpact.testproject.entity.OrderEntity;
import com.primpact.testproject.model.OrderRequest;
import com.primpact.testproject.model.OrderResponse;
import com.primpact.testproject.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// CHANGE SCENARIO: BUSINESS LOGIC
@Service
public class OrderService {

    // BUSINESS LOGIC CHANGED: Thresholds lowered significantly
    private static final BigDecimal DISCOUNT_THRESHOLD = BigDecimal.valueOf(500);
    private static final BigDecimal HIGH_VALUE_THRESHOLD = BigDecimal.valueOf(2500);
    private static final BigDecimal PREMIUM_THRESHOLD = BigDecimal.valueOf(5000);

    private final OrderRepository orderRepository;
    private final FakePaymentClient paymentClient;
    // CIRCULAR DEPENDENCY: OrderService -> UserService -> OrderService
    private final UserService userService;

    @Value("${feature.export.enabled:false}")
    private boolean exportEnabled;

    public OrderService(OrderRepository orderRepository, FakePaymentClient paymentClient,
                        @org.springframework.context.annotation.Lazy UserService userService) {
        this.orderRepository = orderRepository;
        this.paymentClient = paymentClient;
        this.userService = userService;
    }

    // CHANGE SCENARIO: BUSINESS LOGIC
    public boolean isDiscountApplicable(BigDecimal amount) {
        if (amount.compareTo(DISCOUNT_THRESHOLD) > 0) {
            return true;
        }
        return false;
    }

    // CHANGE SCENARIO: BUSINESS LOGIC
    public String categorizeOrder(BigDecimal amount, String customerType, int orderCount) {
        if (amount == null) {
            return "INVALID";
        }

        if (amount.compareTo(PREMIUM_THRESHOLD) > 0) {
            if (customerType != null && customerType.equals("VIP")) {
                if (orderCount > 10) {
                    return "PREMIUM_VIP_LOYAL";
                } else {
                    return "PREMIUM_VIP";
                }
            } else {
                return "PREMIUM";
            }
        } else if (amount.compareTo(HIGH_VALUE_THRESHOLD) > 0) {
            if (customerType != null && customerType.equals("VIP")) {
                return "HIGH_VALUE_VIP";
            } else {
                return "HIGH_VALUE";
            }
        } else if (amount.compareTo(DISCOUNT_THRESHOLD) > 0) {
            return "STANDARD";
        } else {
            return "SMALL";
        }
    }

    // CHANGE SCENARIO: BUSINESS LOGIC
    @Transactional
    public OrderResponse processOrder(OrderRequest request) {
        OrderEntity order = new OrderEntity(request.getAmount());
        
        boolean paymentSuccess = paymentClient.processPayment(null, request.getAmount());
        
        if (paymentSuccess) {
            order.setStatus("CONFIRMED");
        } else {
            order.setStatus("PAYMENT_FAILED");
        }
        
        OrderEntity savedOrder = orderRepository.save(order);
        
        OrderResponse response = new OrderResponse(
                savedOrder.getId(),
                savedOrder.getAmount(),
                savedOrder.getStatus(),
                savedOrder.getCreatedAt()
        );
        response.setDiscountApplied(isDiscountApplicable(savedOrder.getAmount()));
        
        return response;
    }

    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrder(Long id) {
        return orderRepository.findById(id)
                .map(entity -> {
                    OrderResponse response = new OrderResponse(
                            entity.getId(),
                            entity.getAmount(),
                            entity.getStatus(),
                            entity.getCreatedAt()
                    );
                    response.setDiscountApplied(isDiscountApplicable(entity.getAmount()));
                    return response;
                });
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(entity -> {
                    OrderResponse response = new OrderResponse(
                            entity.getId(),
                            entity.getAmount(),
                            entity.getStatus(),
                            entity.getCreatedAt()
                    );
                    response.setDiscountApplied(isDiscountApplicable(entity.getAmount()));
                    return response;
                })
                .toList();
    }

    // CHANGE SCENARIO: CONFIG
    public boolean isExportEnabled() {
        return exportEnabled;
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, String newStatus) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        
        order.setStatus(newStatus);
        OrderEntity savedOrder = orderRepository.save(order);
        
        OrderResponse response = new OrderResponse(
                savedOrder.getId(),
                savedOrder.getAmount(),
                savedOrder.getStatus(),
                savedOrder.getCreatedAt()
        );
        response.setDiscountApplied(isDiscountApplicable(savedOrder.getAmount()));
        
        return response;
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException("Order not found: " + id);
        }
        orderRepository.deleteById(id);
    }
}
