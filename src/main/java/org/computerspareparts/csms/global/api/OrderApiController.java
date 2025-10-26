package org.computerspareparts.csms.global.api;

import org.computerspareparts.csms.global.dto.OrderItemDTO;
import org.computerspareparts.csms.global.dto.OrderRequestDTO;
import org.computerspareparts.csms.global.entity.CustomerOrder;
import org.computerspareparts.csms.global.service.OrderService;
import org.computerspareparts.csms.global.service.CustomerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerOrderService customerOrderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO request, Authentication auth) {
        try {
            // Require authentication; create order for logged-in user regardless of client-supplied customerId
            if (auth == null || auth.getName() == null) {
                return ResponseEntity.status(401).body(java.util.Map.of("error", "Unauthenticated"));
            }

            // Convert items to Map<String,Integer> (partId -> quantity) expected by CustomerOrderService
            Map<String, Integer> cart = new HashMap<>();
            if (request.getItems() != null) {
                for (OrderItemDTO it : request.getItems()) {
                    if (it.getPartId() != null && it.getQuantity() != null) {
                        cart.put(String.valueOf(it.getPartId()), it.getQuantity());
                    }
                }
            }

            CustomerOrder created = customerOrderService.createOrder(auth.getName(), cart);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listMyOrders(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(401).body(java.util.Map.of("error", "Unauthenticated"));
        }
        try {
            String email = auth.getName();
            List<CustomerOrder> orders = customerOrderService.findOrdersByUserEmail(email);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
