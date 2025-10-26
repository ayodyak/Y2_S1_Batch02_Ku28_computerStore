package org.computerspareparts.csms.global.repository;

import org.computerspareparts.csms.global.entity.SupplierPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierPartRepository extends JpaRepository<SupplierPart, Integer> {
}

