package com.medichub.controller.cart;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class CheckoutController {

    @GetMapping("/checkout")
    public String checkout(Principal principal, RedirectAttributes redirectAttributes) {

        // Principal is null if no user is logged in.
        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must log in first to proceed to checkout!");
            return "redirect:/cart";
        }
        // User is logged in → checkout page
        return "cart/checkout";
    }

}
