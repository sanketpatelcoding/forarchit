package com.sanket.patel.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.sanket.patel.repositories.EmployeeRepository;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/employees")
    public String viewEmployees(Model model) {
        List<Map<String, Object>> employees = employeeRepository.findAll();
        System.out.println("✅ Controller hit. Found: " + employees.size());
        model.addAttribute("employees", employees);
        return "employee-list";
    }

    @GetMapping("/")
    public String redirectToEmployees() {
        System.out.println("🔁 Redirecting to /employees");
        return "redirect:/employees";
    }

}
