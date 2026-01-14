package com.example.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/auth/login")
    public String login() {
        return "Public endpoint";
    }

    // защищенный
    @GetMapping("/protected/resource")
    public String protectedResource() {
        return "Protected endpoint";
    }
}
