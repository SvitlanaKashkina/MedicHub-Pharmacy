package com.medichub.controller.auth;

import com.medichub.dto.auth.SignUpRequestDTO;
import com.medichub.dto.auth.UserDTO;
import com.medichub.security.CustomUserDetails;
import com.medichub.security.CustomUserDetailsService;
import com.medichub.service.user.UserService;
import jakarta.validation.Valid;
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

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

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

        log.info("Called signup for user's email: " + request.getEmail());

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/auth/signup";
        }


        userService.registerUser(request);
        redirectAttributes.addFlashAttribute("success", "Registration successful!");

        log.info("User registered successfully!");

        return "redirect:/auth/login";
    }

    @GetMapping("/account")
    public String showAccountPage(Model model) {

        log.info("Called account page");

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(userDetails.getUsername()); // Username = Email
        userDTO.setFirstname(userDetails.getFirstname());
        userDTO.setLastname(userDetails.getLastname());
        userDTO.setRole(userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("USER"));

        model.addAttribute("user", userDTO);

        return "user/account";
    }

}
