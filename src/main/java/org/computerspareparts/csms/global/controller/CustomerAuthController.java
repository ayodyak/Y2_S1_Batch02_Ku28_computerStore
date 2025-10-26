package org.computerspareparts.csms.global.controller;


import org.computerspareparts.csms.global.entity.User;
import org.computerspareparts.csms.global.entity.Role;
import org.computerspareparts.csms.global.service.UserService;
import org.computerspareparts.csms.global.factory.UserRoleFactory;
import org.computerspareparts.csms.global.factory.UserRoleHandler;
import org.computerspareparts.csms.global.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer")
public class CustomerAuthController {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "customer/login";  // thymeleaf: templates/customer/login.html
    }


    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("user", new User());
        return "customer/signup";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        userService.registerCustomer(user);
        return "redirect:/customer/login?registered";
    }

    @GetMapping("/home")
    public String customerHome(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        UserRoleHandler handler = UserRoleFactory.getHandler(user.getRole());
        return "redirect:" + handler.getDashboardUrl();
    }
}
