package com.ecommerce.service;

import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void save(User u) {
        if (u.getId() == 0) { 
            u.setPassword(passwordEncoder.encode(u.getPassword()));
        }
        userRepo.save(u);
    }

    public List<User> fetchAll() {
        return userRepo.findAll();
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userRepo.findByEmail(email));
    }
}