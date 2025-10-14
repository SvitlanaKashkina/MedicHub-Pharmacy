package com.medichub.controller.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class LogInController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/logIn";
    }
 /*
    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard"; // eigene Seite nach Login
    }  */
}
