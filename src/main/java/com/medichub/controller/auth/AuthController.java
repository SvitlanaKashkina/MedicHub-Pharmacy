package com.medichub.controller.auth;

import com.medichub.dto.auth.SignUpRequestDTO;
import com.medichub.dto.auth.UserDTO;
import com.medichub.model.User;
import com.medichub.security.CustomUserDetails;
import com.medichub.security.CustomUserDetailsService;
import com.medichub.service.auth.AuthService;
import com.medichub.service.user.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

        String email = principal.getName();

        User user = authService.findByEmail(email);

        model.addAttribute("user", user);

        return "/user/account";
    }
}
