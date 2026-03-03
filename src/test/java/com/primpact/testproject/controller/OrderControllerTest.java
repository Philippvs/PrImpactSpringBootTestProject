package com.primpact.testproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.primpact.testproject.model.OrderRequest;
import com.primpact.testproject.model.OrderResponse;
import com.primpact.testproject.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Nested
    @DisplayName("POST /api/orders tests")
    class CreateOrderTests {

        @Test
        @WithMockUser(username = "user", roles = "USER")
        @DisplayName("Should create order with valid request")
        void shouldCreateOrderWithValidRequest() throws Exception {
            OrderRequest request = new OrderRequest(BigDecimal.valueOf(1500));
            OrderResponse response = createOrderResponse(1L, BigDecimal.valueOf(1500), "CONFIRMED");
            
            when(orderService.processOrder(any(OrderRequest.class))).thenReturn(response);
            
            mockMvc.perform(post("/api/orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.status").value("CONFIRMED"))
                    .andExpect(jsonPath("$.discountApplied").value(true));
        }

        @Test
        @WithMockUser(username = "user", roles = "USER")
        @DisplayName("Should return bad request for invalid amount")
        void shouldReturnBadRequestForInvalidAmount() throws Exception {
            OrderRequest request = new OrderRequest(BigDecimal.valueOf(-100));
            
            mockMvc.perform(post("/api/orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return forbidden/unauthorized without authentication")
        void shouldReturnUnauthorizedWithoutAuth() throws Exception {
            OrderRequest request = new OrderRequest(BigDecimal.valueOf(1500));
            
            mockMvc.perform(post("/api/orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "user", roles = "USER")
        @DisplayName("Should return forbidden without CSRF token")
        void shouldReturnForbiddenWithoutCsrf() throws Exception {
            OrderRequest request = new OrderRequest(BigDecimal.valueOf(1500));
            
            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/orders/{id} tests")
    class GetOrderTests {

        @Test
        @WithMockUser(username = "user", roles = "USER")
        @DisplayName("Should return order when found")
        void shouldReturnOrderWhenFound() throws Exception {
            Long orderId = 1L;
            OrderResponse response = createOrderResponse(orderId, BigDecimal.valueOf(500), "CONFIRMED");
            
            when(orderService.getOrder(orderId)).thenReturn(Optional.of(response));
            
            mockMvc.perform(get("/api/orders/{id}", orderId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(orderId))
                    .andExpect(jsonPath("$.status").value("CONFIRMED"));
        }

        @Test
        @WithMockUser(username = "user", roles = "USER")
        @DisplayName("Should return 404 when order not found")
        void shouldReturn404WhenNotFound() throws Exception {
            Long orderId = 999L;
            
            when(orderService.getOrder(orderId)).thenReturn(Optional.empty());
            
            mockMvc.perform(get("/api/orders/{id}", orderId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return unauthorized without authentication")
        void shouldReturnUnauthorizedWithoutAuth() throws Exception {
            mockMvc.perform(get("/api/orders/{id}", 1L))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /api/orders tests")
    class GetAllOrdersTests {

        @Test
        @WithMockUser(username = "user", roles = "USER")
        @DisplayName("Should return all orders")
        void shouldReturnAllOrders() throws Exception {
            List<OrderResponse> orders = List.of(
                    createOrderResponse(1L, BigDecimal.valueOf(500), "CONFIRMED"),
                    createOrderResponse(2L, BigDecimal.valueOf(1500), "PENDING")
            );
            
            when(orderService.getAllOrders()).thenReturn(orders);
            
            mockMvc.perform(get("/api/orders"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[1].id").value(2));
        }
    }

    @Nested
    @DisplayName("PUT /api/orders/{id}/status tests")
    class UpdateOrderStatusTests {

        @Test
        @WithMockUser(username = "user", roles = "USER")
        @DisplayName("Should update order status")
        void shouldUpdateOrderStatus() throws Exception {
            Long orderId = 1L;
            OrderResponse response = createOrderResponse(orderId, BigDecimal.valueOf(500), "SHIPPED");
            
            when(orderService.updateOrderStatus(eq(orderId), eq("SHIPPED"))).thenReturn(response);
            
            mockMvc.perform(put("/api/orders/{id}/status", orderId)
                            .with(csrf())
                            .param("status", "SHIPPED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SHIPPED"));
        }

        @Test
        @WithMockUser(username = "user", roles = "USER")
        @DisplayName("Should return 404 when order not found")
        void shouldReturn404WhenNotFound() throws Exception {
            Long orderId = 999L;
            
            when(orderService.updateOrderStatus(eq(orderId), any()))
                    .thenThrow(new IllegalArgumentException("Order not found"));
            
            mockMvc.perform(put("/api/orders/{id}/status", orderId)
                            .with(csrf())
                            .param("status", "SHIPPED"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/orders/{id} tests")
    class DeleteOrderTests {

        @Test
        @WithMockUser(username = "user", roles = "USER")
        @DisplayName("Should delete order successfully")
        void shouldDeleteOrder() throws Exception {
            Long orderId = 1L;
            
            mockMvc.perform(delete("/api/orders/{id}", orderId)
                            .with(csrf()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(username = "user", roles = "USER")
        @DisplayName("Should return 404 when order not found")
        void shouldReturn404WhenNotFound() throws Exception {
            Long orderId = 999L;
            
            org.mockito.Mockito.doThrow(new IllegalArgumentException("Order not found"))
                    .when(orderService).deleteOrder(orderId);
            
            mockMvc.perform(delete("/api/orders/{id}", orderId)
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }
    }

    private OrderResponse createOrderResponse(Long id, BigDecimal amount, String status) {
        OrderResponse response = new OrderResponse(id, amount, status, LocalDateTime.now());
        response.setDiscountApplied(amount.compareTo(BigDecimal.valueOf(1000)) > 0);
        return response;
    }
}
