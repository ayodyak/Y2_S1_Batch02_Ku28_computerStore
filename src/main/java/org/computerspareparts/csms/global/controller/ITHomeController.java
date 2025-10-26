package org.computerspareparts.csms.global.controller;
import org.computerspareparts.csms.global.service.PartService;
import org.computerspareparts.csms.global.entity.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/employee")
public class ITHomeController {

    @GetMapping("/it/dashboard")
    public String itDashboard() {
        return "employee/it_dashboard";
    }

}
