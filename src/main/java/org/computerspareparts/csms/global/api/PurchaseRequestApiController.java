package org.computerspareparts.csms.global.api;

import org.computerspareparts.csms.global.dto.PurchaseRequestDto;
import org.computerspareparts.csms.global.dto.PurchaseRequestItemDto;
import org.computerspareparts.csms.global.entity.PurchaseRequest;
import org.computerspareparts.csms.global.entity.User;
import org.computerspareparts.csms.global.repository.UserRepository;
import org.computerspareparts.csms.global.repository.EmployeeRepository;
import org.computerspareparts.csms.global.repository.PartRepository;
import org.computerspareparts.csms.global.service.PurchaseRequestService;
import org.computerspareparts.csms.global.repository.SupplierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/purchase-requests")
public class PurchaseRequestApiController {

    private static final Logger log = LoggerFactory.getLogger(PurchaseRequestApiController.class);

    private final PurchaseRequestService prService;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final SupplierRepository supplierRepository;
    private final PartRepository partRepository;

    public PurchaseRequestApiController(PurchaseRequestService prService, UserRepository userRepository, EmployeeRepository employeeRepository, SupplierRepository supplierRepository, PartRepository partRepository) {
        this.prService = prService;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.supplierRepository = supplierRepository;
        this.partRepository = partRepository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String,Object>>> list(@RequestParam(value = "supplier_id", required = false) Integer supplierId) {
        List<PurchaseRequest> all = prService.findAll();

        // If supplier_id provided, filter requests for that supplier
        if (supplierId != null) {
            all = all.stream()
                    .filter(pr -> pr.getSupplier() != null && pr.getSupplier().getSupplierId() != null && pr.getSupplier().getSupplierId().intValue() == supplierId)
                    .toList();
        }

        List<Map<String,Object>> out = all.stream().map(this::toMap).toList();
        return ResponseEntity.ok(out);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String,Object>> get(@PathVariable Integer id) {
        return prService.findById(id).map(pr -> ResponseEntity.ok(toMap(pr))).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PurchaseRequestDto dto, Authentication auth) {
        try {
            // If managerId not provided, derive from authenticated user
            if (dto.managerId == null) {
                if (auth == null) return ResponseEntity.status(401).build();
                User u = userRepository.findByEmail(auth.getName()).orElseThrow();
                Optional<org.computerspareparts.csms.global.entity.Employee> e = employeeRepository.findByUser(u);
                if (e.isPresent()) {
                    dto.managerId = e.get().getEmployeeId().intValue();
                } else {
                    // cannot create purchase request without manager
                    return ResponseEntity.badRequest().body(Map.of("error","Authenticated user is not an employee/manager"));
                }
            }

            // Validate supplier exists
            if (dto.supplierId == null || !supplierRepository.existsById(dto.supplierId.longValue())) {
                return ResponseEntity.badRequest().body(Map.of("error","Supplier not found"));
            }

            // Validate manager exists (employee)
            if (!employeeRepository.existsById(dto.managerId.longValue())) {
                return ResponseEntity.badRequest().body(Map.of("error","Manager (employee) not found"));
            }

            // Validate items
            if (dto.items == null || dto.items.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error","No items provided"));
            }
            for (PurchaseRequestItemDto it : dto.items) {
                if (it.partId == null) return ResponseEntity.badRequest().body(Map.of("error","Item missing partId"));
                if (!partRepository.existsById(it.partId)) return ResponseEntity.badRequest().body(Map.of("error","Part not found: " + it.partId));
                if (it.quantity == null || it.quantity <= 0) return ResponseEntity.badRequest().body(Map.of("error","Invalid quantity for part: " + it.partId));
            }

            PurchaseRequest saved = prService.createFromDto(dto);
            return ResponseEntity.status(201).body(toMap(saved));
        } catch (Exception ex) {
            log.error("Failed to create purchase request", ex);
            Map<String,Object> err = new HashMap<>();
            err.put("error", ex.getMessage());
            if (ex.getCause() != null) err.put("cause", ex.getCause().toString());
            return ResponseEntity.status(500).body(err);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean ok = prService.delete(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Integer id) {
        try {
            PurchaseRequest updated = prService.updateStatus(id, "APPROVED");
            return ResponseEntity.ok(toMap(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Integer id) {
        try {
            PurchaseRequest updated = prService.updateStatus(id, "CANCELLED");
            return ResponseEntity.ok(toMap(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        }
    }

    private Map<String,Object> toMap(PurchaseRequest pr) {
        Map<String,Object> m = new HashMap<>();
        m.put("requestId", pr.getRequestId());
        m.put("managerId", pr.getManager() != null ? pr.getManager().getEmployeeId() : null);
        // include manager name if available
        m.put("managerName", pr.getManager() != null ? pr.getManager().getName() : null);
        m.put("supplierId", pr.getSupplier() != null ? pr.getSupplier().getSupplierId() : null);
        // include supplier name if available
        m.put("supplierName", pr.getSupplier() != null ? pr.getSupplier().getName() : null);
        m.put("requestDate", pr.getRequestDate() != null ? pr.getRequestDate().toString() : null);
        m.put("status", pr.getStatus() != null ? pr.getStatus().toUpperCase() : null);
        m.put("totalAmount", pr.getTotalAmount());
        List<Map<String,Object>> items = pr.getItems().stream().map(i -> {
            Map<String,Object> im = new HashMap<>();
            im.put("id", i.getId());
            im.put("partId", i.getPart() != null ? i.getPart().getPartId() : null);
            im.put("partName", i.getPart() != null ? i.getPart().getName() : null);
            im.put("quantity", i.getQuantity());
            im.put("unitPrice", i.getUnitPrice());
            im.put("status", i.getStatus() != null ? i.getStatus().toUpperCase() : null);
            return im;
        }).collect(Collectors.toList());
        m.put("items", items);
        return m;
    }
}
