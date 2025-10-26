package org.computerspareparts.csms.global.repository;

import org.computerspareparts.csms.global.entity.SupplierDeliveryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierDeliveryItemRepository extends JpaRepository<SupplierDeliveryItem, Integer> {
}

