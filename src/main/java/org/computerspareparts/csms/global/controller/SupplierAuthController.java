package org.computerspareparts.csms.global.controller;



import org.computerspareparts.csms.global.entity.User;
import org.computerspareparts.csms.global.entity.Role;
import org.computerspareparts.csms.global.repository.UserRepository;
import org.computerspareparts.csms.global.factory.UserRoleFactory;
import org.computerspareparts.csms.global.factory.UserRoleHandler;
import org.computerspareparts.csms.global.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/supplier")
public class SupplierAuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "supplier/login";  // templates/supplier/login.html
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("user", new User());
        return "supplier/signup";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        // Ensure role is SUPPLIER and save using registerSupplier to create profile
        user.setRole(Role.SUPPLIER);
        userService.registerSupplier(user);
        return "redirect:/supplier/login?registered";
    }

    @GetMapping("/home")
    public String supplierHome(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        UserRoleHandler handler = UserRoleFactory.getHandler(user.getRole());
        return "redirect:" + handler.getDashboardUrl();
    }
}
