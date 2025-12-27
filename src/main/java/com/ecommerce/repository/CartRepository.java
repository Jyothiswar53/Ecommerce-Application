package com.ecommerce.repository;

import com.ecommerce.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserEmail(String userEmail);
    void deleteByUserEmailAndProductId(String userEmail, Long productId);
    Cart findByUserEmailAndProductId(String userEmail, Long productId);
}