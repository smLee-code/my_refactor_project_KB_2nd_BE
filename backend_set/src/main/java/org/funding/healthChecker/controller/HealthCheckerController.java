package org.funding.healthChecker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckerController {

    @GetMapping("/health")
    public String healthCheck() {
        return "healthy";
    }
}
