package com.medichub.service.order;

import com.medichub.model.*;
import com.medichub.repository.*;
import com.medichub.service.cart.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;



@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;

    @Transactional
    public void processOrder(String username, String street, String city, String zip, String country,
                             String cardNumber, String expiryDate, String cvv) {

        log.info("OrderService: Processing order for user: {}", username);

        // Find user
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Find cart
        Cart cart = cartRepository.findByUser_UserId(user.getUserId())
                .orElseGet(() -> {
                    log.warn("No cart found for user: {}. Creating a new one.", username);
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Calculate total amount
        double totalPrice = cart.getCartItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        // Save address
        Address address = new Address();
        address.setStreet(street);
        address.setCity(city);
        address.setZip(zip);
        address.setCountry(country);
        addressRepository.save(address);

        // Save payment
        Payment payment = new Payment();
        payment.setCardNumber(cardNumber);
        payment.setExpiryDate(expiryDate);
        payment.setCvv(cvv);
        payment.setAmount(totalPrice);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("PAID");
        paymentRepository.save(payment);

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setStatus("PAID");
        order.setTotalPrice(totalPrice);
        order.setCreatedAt(LocalDateTime.now());
        order.setAddress(address);
        order.setPayment(payment);
        orderRepository.save(order);

        // Create order items
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItemRepository.save(orderItem);
        }

        // Clear cart
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cartRepository.save(cart);
        cartService.clearCart(user);
        log.info("Order successfully processed for user: {}", username);
    }
}
