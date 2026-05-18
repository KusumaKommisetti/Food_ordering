package com.foodapp.controller;

import com.foodapp.dto.OrderRequest;
import com.foodapp.model.*;
import com.foodapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest request) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) return ResponseEntity.badRequest().build();

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PLACED");
        order.setDeliveryAddress(request.getDeliveryAddress() != null ? request.getDeliveryAddress() : user.getAddress());
        double total = 0;

        for (OrderRequest.CartItem ci : request.getItems()) {
            Product product = productRepository.findById(ci.getProductId()).orElse(null);
            if (product == null) continue;
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(ci.getQuantity());
            item.setPrice(product.getPrice() * ci.getQuantity());
            order.getItems().add(item);
            total += item.getPrice();
        }

        order.setTotalAmount(total);
        return ResponseEntity.ok(orderRepository.save(order));
    }

    @GetMapping("/user/{userId}")
    public List<Order> getUserOrders(@PathVariable Long userId) {
        return orderRepository.findByUserIdOrderByOrderTimeDesc(userId);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderTimeDesc();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(status);
            if ("DELIVERED".equals(status)) {
                order.setDeliveredTime(java.time.LocalDateTime.now());
            }
            return ResponseEntity.ok(orderRepository.save(order));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/reviewed")
    public ResponseEntity<?> markReviewed(@PathVariable Long id) {
        return orderRepository.findById(id).map(order -> {
            order.setReviewed(true);
            return ResponseEntity.ok(orderRepository.save(order));
        }).orElse(ResponseEntity.notFound().build());
    }
}
