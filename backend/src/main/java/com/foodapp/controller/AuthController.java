package com.foodapp.controller;
 
import com.foodapp.dto.LoginRequest;
import com.foodapp.model.User;
import com.foodapp.repository.UserRepository;
import com.foodapp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
@RestController
@RequestMapping("/api/auth")
public class AuthController {
 
    @Autowired
    private UserRepository userRepository;
 
    @Autowired
    private JwtUtil jwtUtil;
 
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already exists"));
        }
        user.setRole("USER");
        User saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }
 
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userRepository.findByEmailAndPassword(request.getEmail(), request.getPassword())
                .map(user -> {
                    String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", user.getId());
                    response.put("name", user.getName());
                    response.put("email", user.getEmail());
                    response.put("role", user.getRole());
                    response.put("phone", user.getPhone());
                    response.put("address", user.getAddress());
                    response.put("token", token);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(401).build());
    }
 
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findByRole("USER");
    }
}