package org.computerspareparts.csms.global.controller;

import org.computerspareparts.csms.global.entity.CustomerOrder;
import org.computerspareparts.csms.global.service.CustomerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/customer")
public class CustomerOrderController {

    private final CustomerOrderService orderService;

    @Autowired
    public CustomerOrderController(CustomerOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order/create")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Integer> cart, Authentication auth) {
        try {
            if (auth == null || auth.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthenticated"));
            }
            CustomerOrder created = orderService.createOrder(auth.getName(), cart);
            Map<String, Object> resp = new HashMap<>();
            resp.put("orderId", created.getOrderId());
            // status is a String in CustomerOrder entity
            resp.put("status", created.getStatus());
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    // GET /customer/orders - list orders for authenticated user
    @GetMapping("/orders")
    public ResponseEntity<?> listMyOrders(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthenticated"));
        }
        try {
            String userEmail = auth.getName();
            List<CustomerOrder> orders = orderService.findOrdersByUserEmail(userEmail);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
