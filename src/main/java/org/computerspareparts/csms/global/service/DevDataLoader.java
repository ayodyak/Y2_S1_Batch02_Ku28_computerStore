package org.computerspareparts.csms.global.service;

import org.computerspareparts.csms.global.entity.Role;
import org.computerspareparts.csms.global.entity.User;
import org.computerspareparts.csms.global.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

@Component
@Profile("dev")
public class DevDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataLoader.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // Manager
        String managerEmail = "manager@example.com";
        String managerPlain = "Manager#2025"; // plaintext password to be displayed in logs (dev only)

        if (userRepository.findByEmail(managerEmail).isEmpty()) {
            User manager = new User();
            manager.setName("Dev Manager");
            manager.setEmail(managerEmail);
            manager.setPassword(managerPlain); // UserService will encode
            manager.setRole(Role.MANAGER);
            manager.setActive(true);
            manager.setPhone("+10000000001");
            manager.setAddress("Manager St, Dev City");
            manager.setCity("DevCity");
            userService.registerEmployee(manager); // creates user + employee profile
            log.info("Created dev manager: {} with password: {}", managerEmail, managerPlain);
        } else {
            log.info("Dev manager already exists: {}", managerEmail);
        }

        // Supplier
        String supplierEmail = "supplier@example.com";
        String supplierPlain = "Supplier#2025";

        if (userRepository.findByEmail(supplierEmail).isEmpty()) {
            User supplier = new User();
            supplier.setName("Dev Supplier");
            supplier.setEmail(supplierEmail);
            supplier.setPassword(supplierPlain);
            supplier.setRole(Role.SUPPLIER);
            supplier.setActive(true);
            supplier.setPhone("+10000000002");
            supplier.setAddress("Supplier Rd, Dev City");
            supplier.setCity("DevCity");
            userService.registerSupplier(supplier); // creates user + supplier profile
            log.info("Created dev supplier: {} with password: {}", supplierEmail, supplierPlain);
        } else {
            log.info("Dev supplier already exists: {}", supplierEmail);
        }

        // Example customer
        String customerEmail = "customer@example.com";
        String customerPlain = "Customer#2025";

        if (userRepository.findByEmail(customerEmail).isEmpty()) {
            User customer = new User();
            customer.setName("Dev Customer");
            customer.setEmail(customerEmail);
            customer.setPassword(customerPlain);
            customer.setRole(Role.CUSTOMER);
            customer.setActive(true);
            customer.setPhone("+10000000003");
            customer.setAddress("Customer Ln, Dev City");
            customer.setCity("DevCity");
            userService.registerCustomer(customer); // creates user + customer profile
            log.info("Created dev customer: {} with password: {}", customerEmail, customerPlain);
        } else {
            log.info("Dev customer already exists: {}", customerEmail);
        }

        log.info("DevDataLoader finished - manager/email: {} , supplier/email: {} , customer/email: {}", managerEmail, supplierEmail, customerEmail);
    }
}
