package org.computerspareparts.csms.global.service;


import org.computerspareparts.csms.global.entity.Role;
import org.computerspareparts.csms.global.entity.User;
import org.computerspareparts.csms.global.entity.Customer;
import org.computerspareparts.csms.global.entity.Employee;
import org.computerspareparts.csms.global.entity.Supplier;
import org.computerspareparts.csms.global.repository.UserRepository;
import org.computerspareparts.csms.global.repository.CustomerRepository;
import org.computerspareparts.csms.global.repository.EmployeeRepository;
import org.computerspareparts.csms.global.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private CustomerRepository customerRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private SupplierRepository supplierRepository;

    // Register a customer and create a linked Customer profile row
    public User registerCustomer(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setActive(true);
        User saved = userRepository.save(user);

        // create linked Customer record if not exists
        Customer c = new Customer();
        c.setUser(saved);
        c.setName(saved.getName());
        customerRepository.save(c);

        return saved;
    }

    // Register an employee - sets role from the submitted user object and encodes the password; creates Employee profile
    public User registerEmployee(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            // default to SALES_STAFF if role not provided
            user.setRole(Role.SALES_STAFF);
        }
        user.setActive(true);
        User saved = userRepository.save(user);

        // create linked Employee record if not exists
        Employee e = new Employee();
        e.setUser(saved);
        e.setName(saved.getName());
        employeeRepository.save(e);

        return saved;
    }

    // Register supplier (sets role SUPPLIER, creates Supplier profile)
    public User registerSupplier(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.SUPPLIER);
        user.setActive(true);
        User saved = userRepository.save(user);

        Supplier s = new Supplier();
        s.setUser(saved);
        s.setName(saved.getName());
        s.setContact(saved.getPhone());
        supplierRepository.save(s);

        return saved;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Update basic profile fields (name, phone, address, city). Returns updated user.
    public User updateProfile(String email, String name, String phone, String address, String city) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (name != null && !name.isBlank()) user.setName(name);
        if (phone != null) user.setPhone(phone);
        if (address != null) user.setAddress(address);
        if (city != null) user.setCity(city);
        return userRepository.save(user);
    }

    // Change password: verifies current password matches, then updates to encoded new password
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
