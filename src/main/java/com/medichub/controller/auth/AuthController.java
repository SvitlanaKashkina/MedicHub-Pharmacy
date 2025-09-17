package com.medichub.controller.auth;

import com.medichub.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    // --- LogIn (Anmeldung) ---
    @GetMapping("/logIn")
    public String showLogInForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/logIn";
    }
}
