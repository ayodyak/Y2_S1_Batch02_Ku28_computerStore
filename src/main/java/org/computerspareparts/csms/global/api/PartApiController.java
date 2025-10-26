package org.computerspareparts.csms.global.api;

import org.computerspareparts.csms.global.entity.Part;
import org.computerspareparts.csms.global.service.PartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/parts")
public class PartApiController {

    private final PartService partService;

    public PartApiController(PartService partService) {
        this.partService = partService;
    }

    // GET /api/parts
    @GetMapping
    public ResponseEntity<List<Part>> getAllParts() {
        return ResponseEntity.ok(partService.findAll());
    }

    // GET /api/parts/low-stock
    @GetMapping("/low-stock")
    public ResponseEntity<List<Part>> getLowStockParts() {
        return ResponseEntity.ok(partService.findLowStockParts());
    }

    // GET /api/parts/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Part> getPart(@PathVariable Integer id) {
        return partService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/parts  (create)
    @PostMapping
    public ResponseEntity<Part> addPart(@RequestBody Part part) {
        try {
            Part saved = partService.createPart(part);
            // Location header optional but helpful
            return ResponseEntity.created(URI.create("/api/parts/" + saved.getPartId())).body(saved);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to create part: " + e.getMessage(), e);
        }
    }

    // PUT /api/parts/{id} (update)
    @PutMapping("/{id}")
    public ResponseEntity<Part> updatePart(@PathVariable Integer id, @RequestBody Part part) {
        try {
            Part updated = partService.updatePart(id, part);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to update part: " + e.getMessage(), e);
        }
    }

    // DELETE /api/parts/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Integer id) {
        boolean deleted = partService.deletePart(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }
}
