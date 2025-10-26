package org.computerspareparts.csms.global.controller;

import org.computerspareparts.csms.global.entity.User;
import org.computerspareparts.csms.global.entity.Supplier;
import org.computerspareparts.csms.global.repository.UserRepository;
import org.computerspareparts.csms.global.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/supplier")
public class SupplierHomeController {

    @Autowired private UserRepository userRepository;
    @Autowired private SupplierRepository supplierRepository;

    @GetMapping("/dashboard")
    public String supplierDashboard(Authentication auth, Model model) {
        // Try to resolve the logged-in user's supplier profile and expose its ID to the template
        try {
            Optional<User> u = userRepository.findByEmail(auth.getName());
            if (u.isPresent()) {
                Optional<Supplier> s = supplierRepository.findByUser(u.get());
                s.ifPresent(sup -> model.addAttribute("currentSupplierId", sup.getSupplierId()));
            }
        } catch (Exception ignored) {
            // If something goes wrong, the template will fall back to the default JS value
        }
        return "supplier/dashboard";
    }
}
