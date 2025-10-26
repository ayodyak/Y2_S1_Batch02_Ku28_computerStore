package org.computerspareparts.csms.global.api;

import org.computerspareparts.csms.global.repository.SupplierRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierApiController {

    private final SupplierRepository supplierRepository;

    public SupplierApiController(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String,Object>>> list() {
        List<Map<String,Object>> out = new ArrayList<>();
        supplierRepository.findAll().forEach(s -> {
            Map<String,Object> m = new HashMap<>();
            m.put("supplierId", s.getSupplierId());
            m.put("name", s.getName());
            m.put("contact", s.getContact());
            out.add(m);
        });
        return ResponseEntity.ok(out);
    }
}
