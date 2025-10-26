package org.computerspareparts.csms.global.repository;

import org.computerspareparts.csms.global.entity.PurchaseRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Integer> {
}

