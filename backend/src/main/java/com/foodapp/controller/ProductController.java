package com.foodapp.controller;

import com.foodapp.model.Product;
import com.foodapp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<Product> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "asc") String sort) {
        if (category != null && !category.isEmpty()) {
            return sort.equals("desc")
                    ? productRepository.findByCategoryOrderByPriceDesc(category)
                    : productRepository.findByCategoryOrderByPriceAsc(category);
        }
        return sort.equals("desc")
                ? productRepository.findAllByOrderByPriceDesc()
                : productRepository.findAllByOrderByPriceAsc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
