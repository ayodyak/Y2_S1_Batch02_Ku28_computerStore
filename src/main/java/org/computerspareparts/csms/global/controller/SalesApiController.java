package org.computerspareparts.csms.global.controller;

import org.computerspareparts.csms.global.dto.OrderDTO;
import org.computerspareparts.csms.global.dto.PaymentRequestDTO;
import org.computerspareparts.csms.global.service.CustomerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SalesApiController {

    private final CustomerOrderService orderService;

    @Autowired
    public SalesApiController(CustomerOrderService orderService) {
        this.orderService = orderService;
    }

    // GET /api/sales/orders - list all orders for sales dashboard
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> listAllOrders() {
        List<OrderDTO> orders = orderService.findAllOrdersForSales();
        return ResponseEntity.ok(orders);
    }

    // PUT /api/sales/orders/{orderId}/status - update order status
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestBody(required = true) java.util.Map<String, String> body, Authentication auth) {
        try {
            String status = body.get("status");
            if (status == null) return ResponseEntity.badRequest().body(java.util.Map.of("error", "Missing status"));
            OrderDTO updated = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("error", e.getMessage()));
        }
    }

    // POST /api/sales/orders/{orderId}/payments - record a payment
    @PostMapping("/orders/{orderId}/payments")
    public ResponseEntity<?> recordPayment(@PathVariable Long orderId, @RequestBody PaymentRequestDTO req, Authentication auth) {
        try {
            String staffEmail = auth != null ? auth.getName() : null;
            OrderDTO updated = orderService.recordPayment(orderId, req, staffEmail);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
