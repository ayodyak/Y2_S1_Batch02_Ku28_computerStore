package org.computerspareparts.csms.global.controller;


import org.computerspareparts.csms.global.entity.User;
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
@RequestMapping("/employee")
public class EmployeeAuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "employee/login";  // templates/employee/login.html
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("user", new User());
        // provide employee-role options (only employee roles)
        model.addAttribute("roles", new org.computerspareparts.csms.global.entity.Role[]{org.computerspareparts.csms.global.entity.Role.MANAGER, org.computerspareparts.csms.global.entity.Role.SALES_STAFF, org.computerspareparts.csms.global.entity.Role.FINANCE_ACCOUNTANT, org.computerspareparts.csms.global.entity.Role.IT_TECHNICIAN});
        return "employee/signup";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        // register employee (encodes password and sets role)
        userService.registerEmployee(user);
        return "redirect:/employee/login?registered";
    }

    @GetMapping("/home")
    public String employeeHome(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        UserRoleHandler handler = UserRoleFactory.getHandler(user.getRole());
        return "redirect:" + handler.getDashboardUrl();
    }
}
