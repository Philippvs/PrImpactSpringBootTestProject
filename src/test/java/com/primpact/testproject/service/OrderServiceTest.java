package com.primpact.testproject.service;

import com.primpact.testproject.client.FakePaymentClient;
import com.primpact.testproject.entity.OrderEntity;
import com.primpact.testproject.model.OrderRequest;
import com.primpact.testproject.model.OrderResponse;
import com.primpact.testproject.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private FakePaymentClient paymentClient;

    @InjectMocks
    private OrderService orderService;

    @Nested
    @DisplayName("isDiscountApplicable tests")
    class IsDiscountApplicableTests {

        @Test
        @DisplayName("Should return true when amount is greater than 1000")
        void shouldReturnTrueWhenAmountGreaterThan1000() {
            BigDecimal amount = BigDecimal.valueOf(1500);
            
            boolean result = orderService.isDiscountApplicable(amount);
            
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false when amount is exactly 1000")
        void shouldReturnFalseWhenAmountEquals1000() {
            BigDecimal amount = BigDecimal.valueOf(1000);
            
            boolean result = orderService.isDiscountApplicable(amount);
            
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false when amount is less than 1000")
        void shouldReturnFalseWhenAmountLessThan1000() {
            BigDecimal amount = BigDecimal.valueOf(500);
            
            boolean result = orderService.isDiscountApplicable(amount);
            
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return true for boundary value 1000.01")
        void shouldReturnTrueForBoundaryValue() {
            BigDecimal amount = new BigDecimal("1000.01");
            
            boolean result = orderService.isDiscountApplicable(amount);
            
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("categorizeOrder tests")
    class CategorizeOrderTests {

        @Test
        @DisplayName("Should return INVALID for null amount")
        void shouldReturnInvalidForNullAmount() {
            String result = orderService.categorizeOrder(null, "REGULAR", 5);
            
            assertThat(result).isEqualTo("INVALID");
        }

        @Test
        @DisplayName("Should return PREMIUM_VIP_LOYAL for high amount VIP with many orders")
        void shouldReturnPremiumVipLoyal() {
            BigDecimal amount = BigDecimal.valueOf(15000);
            
            String result = orderService.categorizeOrder(amount, "VIP", 15);
            
            assertThat(result).isEqualTo("PREMIUM_VIP_LOYAL");
        }

        @Test
        @DisplayName("Should return PREMIUM_VIP for high amount VIP with few orders")
        void shouldReturnPremiumVip() {
            BigDecimal amount = BigDecimal.valueOf(15000);
            
            String result = orderService.categorizeOrder(amount, "VIP", 5);
            
            assertThat(result).isEqualTo("PREMIUM_VIP");
        }

        @Test
        @DisplayName("Should return HIGH_VALUE_VIP for medium-high amount VIP")
        void shouldReturnHighValueVip() {
            BigDecimal amount = BigDecimal.valueOf(7000);
            
            String result = orderService.categorizeOrder(amount, "VIP", 5);
            
            assertThat(result).isEqualTo("HIGH_VALUE_VIP");
        }

        @Test
        @DisplayName("Should return SMALL for low amount")
        void shouldReturnSmallForLowAmount() {
            BigDecimal amount = BigDecimal.valueOf(500);
            
            String result = orderService.categorizeOrder(amount, "REGULAR", 1);
            
            assertThat(result).isEqualTo("SMALL");
        }
    }

    @Nested
    @DisplayName("processOrder tests")
    class ProcessOrderTests {

        @Test
        @DisplayName("Should create order with CONFIRMED status when payment succeeds")
        void shouldCreateConfirmedOrderWhenPaymentSucceeds() {
            OrderRequest request = new OrderRequest(BigDecimal.valueOf(500));
            OrderEntity savedEntity = createOrderEntity(1L, BigDecimal.valueOf(500), "CONFIRMED");
            
            when(paymentClient.processPayment(any(), any())).thenReturn(true);
            when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedEntity);
            
            OrderResponse result = orderService.processOrder(request);
            
            assertThat(result.getStatus()).isEqualTo("CONFIRMED");
            verify(paymentClient).processPayment(any(), eq(BigDecimal.valueOf(500)));
            verify(orderRepository).save(any(OrderEntity.class));
        }

        @Test
        @DisplayName("Should create order with PAYMENT_FAILED status when payment fails")
        void shouldCreateFailedOrderWhenPaymentFails() {
            OrderRequest request = new OrderRequest(BigDecimal.valueOf(500));
            OrderEntity savedEntity = createOrderEntity(1L, BigDecimal.valueOf(500), "PAYMENT_FAILED");
            
            when(paymentClient.processPayment(any(), any())).thenReturn(false);
            when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedEntity);
            
            OrderResponse result = orderService.processOrder(request);
            
            assertThat(result.getStatus()).isEqualTo("PAYMENT_FAILED");
        }
    }

    @Nested
    @DisplayName("getOrder tests")
    class GetOrderTests {

        @Test
        @DisplayName("Should return order when found")
        void shouldReturnOrderWhenFound() {
            Long orderId = 1L;
            OrderEntity entity = createOrderEntity(orderId, BigDecimal.valueOf(1500), "CONFIRMED");
            
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(entity));
            
            Optional<OrderResponse> result = orderService.getOrder(orderId);
            
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(orderId);
            assertThat(result.get().isDiscountApplied()).isTrue();
        }

        @Test
        @DisplayName("Should return empty when order not found")
        void shouldReturnEmptyWhenNotFound() {
            Long orderId = 999L;
            
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
            
            Optional<OrderResponse> result = orderService.getOrder(orderId);
            
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("updateOrderStatus tests")
    class UpdateOrderStatusTests {

        @Test
        @DisplayName("Should update order status successfully")
        void shouldUpdateOrderStatus() {
            Long orderId = 1L;
            OrderEntity entity = createOrderEntity(orderId, BigDecimal.valueOf(500), "PENDING");
            OrderEntity updatedEntity = createOrderEntity(orderId, BigDecimal.valueOf(500), "SHIPPED");
            
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(entity));
            when(orderRepository.save(any(OrderEntity.class))).thenReturn(updatedEntity);
            
            OrderResponse result = orderService.updateOrderStatus(orderId, "SHIPPED");
            
            assertThat(result.getStatus()).isEqualTo("SHIPPED");
        }

        @Test
        @DisplayName("Should throw exception when order not found")
        void shouldThrowExceptionWhenNotFound() {
            Long orderId = 999L;
            
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
            
            assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, "SHIPPED"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Order not found");
        }
    }

    private OrderEntity createOrderEntity(Long id, BigDecimal amount, String status) {
        OrderEntity entity = new OrderEntity(amount);
        entity.setId(id);
        entity.setStatus(status);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }
}
