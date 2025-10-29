package com.medichub.service.order;

import com.medichub.controller.cart.CartController;
import com.medichub.model.*;
import com.medichub.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private final AddressRepository addressRepository;
    @Autowired
    private final PaymentRepository paymentRepository;
    @Autowired
    private final OrderItemRepository orderItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private final CartItemRepository cartItemRepository;

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Transactional
    public void processOrder(String username, String street, String city, String zip, String country,
                             String cardNumber, String expiryDate, String cvv) {
        log.info("Processing order for user: {}", username);

        // find user
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // cart download
        Cart cart = cartRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        if(cart.getCartItems().isEmpty()) {
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
        List<CartItem> cartItems = cart.getCartItems();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItemRepository.save(orderItem);
        }

       // Empty shopping cart
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }
}
