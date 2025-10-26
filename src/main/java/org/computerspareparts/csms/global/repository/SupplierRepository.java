package org.computerspareparts.csms.global.repository;

import org.computerspareparts.csms.global.entity.Supplier;
import org.computerspareparts.csms.global.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByUser(User user);
}

