package org.computerspareparts.csms.global.controller;

import org.computerspareparts.csms.global.dto.SalesReportCreateRequest;
import org.computerspareparts.csms.global.dto.SalesReportDTO;
import org.computerspareparts.csms.global.service.SalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class SalesReportController {

    @Autowired
    private SalesReportService salesReportService;

    // List reports created by current user
    @GetMapping
    public ResponseEntity<List<SalesReportDTO>> listReports(Authentication auth) {
        String email = auth != null ? auth.getName() : null;
        List<SalesReportDTO> reports = salesReportService.listReportsForUser(email);
        return ResponseEntity.ok(reports);
    }

    // Get a specific report
    @GetMapping("/{id}")
    public ResponseEntity<?> getReport(@PathVariable Long id) {
        java.util.Optional<SalesReportDTO> opt = salesReportService.findById(id);
        if (opt.isPresent()) {
            return ResponseEntity.ok(opt.get());
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Report not found"));
        }
    }

    // Create a report from payment receipts in the date range
    @PostMapping
    public ResponseEntity<?> createReport(@RequestBody SalesReportCreateRequest req, Authentication auth) {
        try {
            String email = auth != null ? auth.getName() : null;
            SalesReportDTO created = salesReportService.createReport(email, req);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {
        salesReportService.deleteReport(id);
        return ResponseEntity.ok(java.util.Map.of("status", "deleted"));
    }
}
