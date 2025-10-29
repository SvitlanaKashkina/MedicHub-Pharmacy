package com.medichub.controller.checkout;


import com.medichub.controller.cart.CartController;
import com.medichub.service.order.OrderService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@AllArgsConstructor
@RequestMapping("/cart/checkout")
public class CheckoutController {

    private final OrderService orderService;

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    @GetMapping
    public String showCheckoutPage(Model model) {
        log.info("Called checkout page");
        return "checkout/checkout";
    }

    @PostMapping("/confirm")
    public String confirmOrder(@RequestParam String street,
                               @RequestParam String city,
                               @RequestParam String zip,
                               @RequestParam String country,
                               @RequestParam String cardNumber,
                               @RequestParam String expiryDate,
                               @RequestParam String cvv,
                               Principal principal, // currently logged-in user
                               Model model) {
        log.info("Called confirm order for user with email: {}", principal.getName());

        if(principal == null) {
            return "redirect:/auth/logIn";
        }

        // Process order
        try {
            orderService.processOrder(principal.getName(), street, city, zip, country, cardNumber, expiryDate, cvv);
            model.addAttribute("message", "Order completed successfully!");
            return "checkout/checkout-success";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "checkout/checkout";
        }
    }
}