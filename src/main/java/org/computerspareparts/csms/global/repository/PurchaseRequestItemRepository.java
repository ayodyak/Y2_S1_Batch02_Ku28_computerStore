package org.computerspareparts.csms.global.repository;

import org.computerspareparts.csms.global.entity.PurchaseRequestItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRequestItemRepository extends JpaRepository<PurchaseRequestItem, Integer> {
}

