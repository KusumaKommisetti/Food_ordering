package com.foodapp.controller;

import com.foodapp.dto.ReviewRequest;
import com.foodapp.model.Product;
import com.foodapp.model.Review;
import com.foodapp.model.User;
import com.foodapp.repository.ProductRepository;
import com.foodapp.repository.ReviewRepository;
import com.foodapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody ReviewRequest request) {
        Product product = productRepository.findById(request.getProductId()).orElse(null);
        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (product == null || user == null) return ResponseEntity.badRequest().build();

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setComment(request.getComment());
        review.setRating(request.getRating());
        return ResponseEntity.ok(reviewRepository.save(review));
    }

    @GetMapping("/product/{productId}")
    public List<Review> getProductReviews(@PathVariable Long productId) {
        return reviewRepository.findByProductId(productId);
    }
}
