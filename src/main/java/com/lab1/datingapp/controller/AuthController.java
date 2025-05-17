package com.lab1.datingapp.controller;

import com.lab1.datingapp.dto.AuthenticationResponse;
import com.lab1.datingapp.jwt.JwtUtil;
import com.lab1.datingapp.model.User;
import com.lab1.datingapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByName(user.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        jwtUtil.generateSecretKeyForUser(user.getName());
        String token = jwtUtil.generateToken(user.getName());
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }

}