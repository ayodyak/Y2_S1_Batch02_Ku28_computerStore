package org.computerspareparts.csms.global.repository;

import org.computerspareparts.csms.global.entity.SupplierDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierDeliveryRepository extends JpaRepository<SupplierDelivery, Integer> {
    List<SupplierDelivery> findBySupplierSupplierId(Integer supplierId);
}

