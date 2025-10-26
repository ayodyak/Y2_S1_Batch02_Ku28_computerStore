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
public class EmployeeHomeController {

    private final PartService partService;

    @Autowired
    public EmployeeHomeController(PartService partService) {
        this.partService = partService;
    }



    // Dashboard
    @GetMapping("/manager/dashboard")
    public String managerDashboard(Model model) {
        // --- PARTS ---
        List<Part> parts = partService.findAll();
        List<Part> lowStockParts = partService.findLowStockParts();
        model.addAttribute("parts", parts);
        model.addAttribute("totalPartsCount", parts.size());
        model.addAttribute("lowStockCount", lowStockParts.size());
        model.addAttribute("lowStockParts", lowStockParts);

        return "employee/manager/manager_dashboard";
    }

    // --- PARTS CRUD ---
    @PostMapping("/manager/part/save")
    public String savePart(@ModelAttribute Part part) {
        if (part.getPartId() == null) {
            partService.createPart(part);
        } else {
            partService.updatePart(part.getPartId(), part);
        }
        return "redirect:/employee/manager/dashboard";
    }

    @GetMapping("/manager/part/{id}")
    @ResponseBody
    public Part getPartById(@PathVariable Integer id) {
        return partService.getPartById(id);
    }

    @PostMapping("/manager/part/delete")
    public String deletePart(@RequestParam Integer id) {
        partService.deletePart(id);
        return "redirect:/employee/manager/dashboard";
    }
}
