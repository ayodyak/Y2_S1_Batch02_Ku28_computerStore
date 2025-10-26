package org.computerspareparts.csms.global.controller;

import org.computerspareparts.csms.global.entity.SupplierPart;
import org.computerspareparts.csms.global.repository.SupplierPartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/supplier")
public class SupplierPartController {

    private final SupplierPartRepository supplierPartRepository;

    @Autowired
    public SupplierPartController(SupplierPartRepository supplierPartRepository) {
        this.supplierPartRepository = supplierPartRepository;
    }

    // GET /api/supplier/parts - returns all supplier parts
    @GetMapping("/parts")
    public ResponseEntity<List<SupplierPart>> listParts(@RequestParam(value = "supplier_id", required = false) Integer supplierId) {
        // supplier_id is optional here — the supplier_part table is a separate table per your DDL
        List<SupplierPart> parts = supplierPartRepository.findAll();
        return ResponseEntity.ok(parts);
    }
}

