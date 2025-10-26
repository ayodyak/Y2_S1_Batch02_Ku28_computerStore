package org.computerspareparts.csms.global.api;

import org.computerspareparts.csms.global.entity.User;
import org.computerspareparts.csms.global.repository.UserRepository;
import org.computerspareparts.csms.global.repository.EmployeeRepository;
import org.computerspareparts.csms.global.repository.SupplierRepository;
import org.computerspareparts.csms.global.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthApiController {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;

    public AuthApiController(UserRepository userRepository,
                             EmployeeRepository employeeRepository,
                             SupplierRepository supplierRepository,
                             CustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.supplierRepository = supplierRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/auth-check")
    public ResponseEntity<Map<String,Object>> authCheck(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        User u = userRepository.findByEmail(auth.getName()).orElse(null);
        if (u == null) return ResponseEntity.status(401).build();
        Map<String,Object> out = new HashMap<>();
        out.put("userId", u.getUserId());
        out.put("email", u.getEmail());
        out.put("role", u.getRole() != null ? u.getRole().name() : null);

        employeeRepository.findByUser(u).ifPresent(e -> out.put("employeeId", e.getEmployeeId()));
        supplierRepository.findByUser(u).ifPresent(s -> out.put("supplierId", s.getSupplierId()));
        customerRepository.findByUser(u).ifPresent(c -> out.put("customerId", c.getCustomerId()));

        return ResponseEntity.ok(out);
    }
}

