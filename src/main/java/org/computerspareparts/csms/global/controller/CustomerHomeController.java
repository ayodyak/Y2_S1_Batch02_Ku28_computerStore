package org.computerspareparts.csms.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/customer")
public class CustomerHomeController {


    @GetMapping({"/dashboard"})
    public String customerDashboard() {
        return "customer/dashboard";
    }
}