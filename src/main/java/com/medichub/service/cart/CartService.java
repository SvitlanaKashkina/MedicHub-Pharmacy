package com.medichub.service.cart;

import com.medichub.exception.ResourceNotFoundException;
import com.medichub.model.CartItem;
import com.medichub.model.Product;
import com.medichub.repository.CartItemRepository;
import com.medichub.repository.CartRepository;
import com.medichub.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    // get all items on the cart page (+ formatted total)
    public List<CartItem> getCartItems() {
        return cartItemRepository.findAll(Sort.by("cartItemId"));
    }

    public String getFormattedTotal(List<CartItem> cartItems) {
        double total = cartItems.stream()
                .mapToDouble(ci -> ci.getProduct().getPrice() * ci.getQuantity())
                .sum();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(total);
    }

    // add product to the Cart by id
    public void addProductToCart(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));

        Optional<CartItem> existing = cartItemRepository.findByProduct(product);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setProduct(product);
            item.setQuantity(1);
            cartItemRepository.save(item);
        }
    }

    //  product quantity (+/-) in the cart
    public void updateCartItemQuantity(Long cartItemId, String action) {
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

        if (!cartItemRepository.existsById(cartItemId)) {
            throw new ResourceNotFoundException("Cart item with ID " + cartItemId + " not found");
        }

        cartItemRepository.deleteById(cartItemId);
    }
}
