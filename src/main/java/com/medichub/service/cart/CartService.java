package com.medichub.service.cart;

import com.medichub.exception.ResourceNotFoundException;
import com.medichub.model.Cart;
import com.medichub.model.CartItem;
import com.medichub.model.Product;
import com.medichub.model.User;
import com.medichub.repository.CartItemRepository;
import com.medichub.repository.CartRepository;
import com.medichub.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public Cart getOrCreateCart(User user) {
        log.info("Getting or creating cart for user: {}", user.getEmail());
        return cartRepository.findByUser_UserId(user.getUserId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    // get all items on the cart page (+ formatted total)
    public List<CartItem> getCartItems() {
        log.info("Getting cart items");
        return cartItemRepository.findAll(Sort.by("cartItemId"));
    }

    public String getFormattedTotal(List<CartItem> cartItems) {
        log.info("Calculating total for cart items: {}", cartItems);
        double total = cartItems.stream()
                .mapToDouble(ci -> ci.getProduct().getPrice() * ci.getQuantity())
                .sum();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(total);
    }

    // add product to the Cart by id
    public void addProductToCart(User user, Long productId) {
        log.info("Adding product with ID: {} to cart for user: {}", productId, user.getEmail());
        Cart cart = cartRepository.findByUser_UserId(user.getUserId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));

        Optional<CartItem> existingItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + 1);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(1);
            cart.getCartItems().add(newItem);
        }

        cartRepository.save(cart); // speichert auch die Items dank CascadeType.ALL
    }

    //  product quantity (+/-) in the cart
    public void updateCartItemQuantity(Long cartItemId, String action) {
        log.info("Updating cart item quantity for cart item with ID: {} with action: {}", cartItemId, action);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid CartItem ID: " + cartItemId));

        if (action.equals("increase")) {
            item.setQuantity(item.getQuantity() + 1);
        } else if (action.equals("decrease") && item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
        }
        cartItemRepository.save(item);
    }

    //Delete a product from the cart
    public void deleteCartItem(Long cartItemId) {
        log.info("Deleting cart item with ID: {}", cartItemId);

        if (!cartItemRepository.existsById(cartItemId)) {
            throw new ResourceNotFoundException("Cart item with ID " + cartItemId + " not found");
        }

        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    public void clearCart(User user) {
        log.info("Clearing cart for user: {}", user.getEmail());
        Cart cart = cartRepository.findByUser_UserId(user.getUserId())
                .orElse(null);

        if (cart != null && !cart.getCartItems().isEmpty()) {
            cartItemRepository.deleteAll(cart.getCartItems());
            cart.getCartItems().clear();
            cartRepository.save(cart);
        }
    }
}
