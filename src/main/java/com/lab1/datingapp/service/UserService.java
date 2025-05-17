package com.lab1.datingapp.service;

import com.lab1.datingapp.model.User;
import com.lab1.datingapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        Optional<User> existingUser = userRepository.findById(user.getId());

        if (existingUser.isPresent()) {
            User updatedUser = existingUser.get();

            updatedUser.setName(user.getName());
            updatedUser.setAge(user.getAge());
            updatedUser.setGender(user.getGender());
            updatedUser.setLocation(user.getLocation());
            updatedUser.setReligion(user.getReligion());

            return userRepository.save(updatedUser);
        } else {
            throw new RuntimeException("User not found for ID: " + user.getId());
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}