package org.computerspareparts.csms.global.repository;

import org.computerspareparts.csms.global.entity.Customer;
import org.computerspareparts.csms.global.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUser(User user);
}
