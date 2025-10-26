package org.computerspareparts.csms.global.repository;

import org.computerspareparts.csms.global.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PartRepository extends JpaRepository<Part, Integer> {

    // Search helper (you already had this but repeat here for completeness)
    List<Part> findByNameContainingIgnoreCase(String name);

    // Return parts that are at or below reorder level
    @Query("SELECT p FROM Part p WHERE p.stockLevel <= p.reorderLevel")
    List<Part> findLowStockParts();
}

