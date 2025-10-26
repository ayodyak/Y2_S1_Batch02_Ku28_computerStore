package org.computerspareparts.csms.global.repository;

import org.computerspareparts.csms.global.entity.Employee;
import org.computerspareparts.csms.global.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUser(User user);
}
