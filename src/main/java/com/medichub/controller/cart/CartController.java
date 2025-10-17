package com.medichub.controller.cart;

import com.medichub.model.CartItem;
import com.medichub.service.cart.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    // Show cart
    @GetMapping
    public String showCart(Model model) {

        log.info("Called cart page");

        List<CartItem> cartItems = cartService.getCartItems();
        String formattedTotal = cartService.getFormattedTotal(cartItems);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalFormatted", formattedTotal);
        return "cart/cart";
    }

    // Add product to the Cart by id
    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId) {

        log.info("Called add to cart for product with ID: " + productId);

        cartService.addProductToCart(productId);
        return "redirect:/cart";
    }

    //  product quantity (+/-) in the cart
    @PostMapping("/update/{id}")
    public String updateCartItem(@PathVariable Long id, @RequestParam String action) {

        log.info("Called update cart item for cart item with ID: " + id);

        cartService.updateCartItemQuantity(id, action);
        return "redirect:/cart";
    }

    //Delete a product from the cart
    @PostMapping("/delete/{id}")
    public String deleteCartItem(@PathVariable Long id) {

        log.info("Called delete cart item for cart item with ID: " + id);

        cartService.deleteCartItem(id);
        return "redirect:/cart";
    }
}
