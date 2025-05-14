package com.sanket.patel.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.*;

@Controller
public class EmployeeController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/")
    public String redirectToEmployees() {
        return "redirect:/employees";
    }

    @GetMapping("/employees")
    public String employeePage() {
        return "employee-list"; // loads employee-list.html
    }

    @PostMapping("/api/employees")
    @ResponseBody
    public Map<String, Object> getPaginatedEmployees(HttpServletRequest request) {
        int start = Integer.parseInt(request.getParameter("start"));
        int length = Integer.parseInt(request.getParameter("length"));
        String searchValue = request.getParameter("search[value]");
        String draw = request.getParameter("draw");

        String baseQuery = "SELECT * FROM employees";
        String countQuery = "SELECT COUNT(*) FROM employees";

        List<Object> params = new ArrayList<>();
        String whereClause = "";

        if (searchValue != null && !searchValue.isEmpty()) {
            whereClause = " WHERE name ILIKE ? OR department ILIKE ?";
            params.add("%" + searchValue + "%");
            params.add("%" + searchValue + "%");
        }

        // Get paginated results
        String pagedQuery = baseQuery + whereClause + " ORDER BY id LIMIT ? OFFSET ?";
        params.add(length);
        params.add(start);
        List<Map<String, Object>> data = jdbcTemplate.queryForList(pagedQuery, params.toArray());

        // Get record counts
        int recordsTotal = jdbcTemplate.queryForObject(countQuery, Integer.class);
        int recordsFiltered = recordsTotal;
        if (!whereClause.isEmpty()) {
            recordsFiltered = jdbcTemplate.queryForObject(countQuery + whereClause,
                    params.subList(0, 2).toArray(), Integer.class);
        }

        // Return response to DataTables
        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", recordsTotal);
        response.put("recordsFiltered", recordsFiltered);
        response.put("data", data);
        return response;
    }
}
