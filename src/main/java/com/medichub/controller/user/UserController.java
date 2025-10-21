package com.medichub.controller.user;

import com.medichub.controller.product.ProductController;
import com.medichub.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //Update Email
    @PostMapping("/update-email")
    public String updateEmail(@RequestParam("newEmail") String newEmail, Principal principal) {

        log.info("Called update email for user with email: {}", principal.getName());

        userService.updateEmail(principal.getName(), newEmail);
        return "redirect:/auth/account?emailUpdated=true";
    }

    //Update Password
    @PostMapping("/update-password")
    public String updatePassword(@RequestParam("newPassword") String newPassword, Principal principal) {

        log.info("Called update password for user with email: {}", principal.getName());

        userService.updatePassword(principal.getName(), newPassword);
        return "redirect:/auth/account?passwordUpdated=true";
    }

    //Delete user
    @PostMapping("/delete")
    public String deleteAccount(Principal principal) {

        log.info("Called delete account for user with email: {}", principal.getName());

        // Check that the user is logged in
        String email = principal.getName();

        userService.deleteUserByEmail(principal.getName());

        return "redirect:/auth/login?accountDeleted=true";
    }
}
