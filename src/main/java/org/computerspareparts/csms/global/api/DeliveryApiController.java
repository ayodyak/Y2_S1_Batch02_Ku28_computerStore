package org.computerspareparts.csms.global.api;

import org.computerspareparts.csms.global.dto.DeliveryRequestDto;
import org.computerspareparts.csms.global.entity.SupplierDelivery;
import org.computerspareparts.csms.global.service.SupplierDeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supplier/deliveries")
public class DeliveryApiController {

    private static final Logger log = LoggerFactory.getLogger(DeliveryApiController.class);

    private final SupplierDeliveryService deliveryService;

    public DeliveryApiController(SupplierDeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping
    public ResponseEntity<?> createDelivery(@RequestBody DeliveryRequestDto dto) {
        try {
            SupplierDelivery d = deliveryService.createDelivery(dto);
            // Build response map minimal (use HashMap to allow null values)
            java.util.Map<String,Object> resp = new java.util.HashMap<>();
            resp.put("deliveryId", d.getDeliveryId());
            resp.put("status", d.getStatus());
            resp.put("requestId", d.getRequest() != null ? d.getRequest().getRequestId() : null);
            return ResponseEntity.status(201).body(resp);
        } catch (IllegalArgumentException e) {
            log.warn("Bad request creating delivery", e);
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception ex) {
            log.error("Failed to create delivery", ex);
            return ResponseEntity.status(500).body(java.util.Map.of("error", ex.getMessage()));
        }
    }

    // New: GET endpoint to list deliveries for a supplier (or all deliveries when supplier_id not provided)
    @GetMapping
    public ResponseEntity<?> listDeliveries(@RequestParam(name = "supplier_id", required = false) Integer supplierId) {
        try {
            java.util.List<java.util.Map<String,Object>> deliveries = deliveryService.listDeliveries(supplierId);
            return ResponseEntity.ok(deliveries);
        } catch (Exception ex) {
            log.error("Failed to list deliveries", ex);
            return ResponseEntity.status(500).body(java.util.Map.of("error", ex.getMessage()));
        }
    }
}
