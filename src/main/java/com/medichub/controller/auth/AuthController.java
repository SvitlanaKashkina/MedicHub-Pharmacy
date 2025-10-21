package com.medichub.controller.auth;

import com.medichub.dto.auth.SignUpRequestDTO;
import com.medichub.model.User;
import com.medichub.service.auth.AuthService;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);


    @GetMapping("/signup")
    public String showSignUpPage() {
        log.info("Called signUp page");
        return "auth/signUp";
    }


    @GetMapping("/login")
    public String showLoginPage() {
        log.info("Called login page");
        return "auth/logIn";
    }


    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignUpRequestDTO request,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {

        //  Validation Errors check
        if (result.hasErrors()) {
            log.warn("Validation errors during signup for email {}: {}",
                    request.getEmail(), result.getAllErrors());
            redirectAttributes.addFlashAttribute("error", result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/auth/signup";
        }

        authService.registerUser(request);

        redirectAttributes.addFlashAttribute("success", "Registration successful!");
        log.info("User with email {} registered successfully", request.getEmail());
        return "redirect:/index";
    }


    @GetMapping("/account")
    public String showAccountPage(Model model, Principal principal) {

        log.info("Called account page");

        if (principal == null) {
            // Redirect to login page if not authenticated
            return "redirect:/auth/logIn";
        }

        String email = principal.getName();
        User user = authService.findByEmail(email);
        model.addAttribute("user", user);

        return "user/account";
    }

}
