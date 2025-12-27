package com.ecommerce.service;

import com.ecommerce.dto.CartItem;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Product;
import com.ecommerce.repository.CartRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    private static final String SESSION_CART_KEY = "sessionCart";

    public List<CartItem> getCartItems(HttpSession session, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            List<Cart> dbCart = cartRepository.findByUserEmail(email);
            return dbCart.stream()
                    .map(this::convertToCartItem)
                    .toList();
        } else {
            List<CartItem> sessionCart = (List<CartItem>) session.getAttribute(SESSION_CART_KEY);
            if (sessionCart == null) {
                sessionCart = new ArrayList<>();
                session.setAttribute(SESSION_CART_KEY, sessionCart);
            }
            return sessionCart;
        }
    }

    public void addToCart(HttpSession session, Authentication authentication, Product product) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Cart existing = cartRepository.findByUserEmailAndProductId(email, product.getId());
            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + 1);
                cartRepository.save(existing);
            } else {
                Cart cart = new Cart();
                cart.setUserEmail(email);
                cart.setProductId(product.getId());
                cart.setProductName(product.getName());
                cart.setPrice(product.getPrice());
                cart.setImageName(product.getImageName());
                cart.setQuantity(1);
                cartRepository.save(cart);
            }
        } else {
            List<CartItem> sessionCart = (List<CartItem>) session.getAttribute(SESSION_CART_KEY);
            if (sessionCart == null) {
                sessionCart = new ArrayList<>();
                session.setAttribute(SESSION_CART_KEY, sessionCart);
            }
            boolean found = false;
            for (CartItem item : sessionCart) {
                if (item.getProductId().equals(product.getId())) {
                    item.setQuantity(item.getQuantity() + 1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                CartItem item = new CartItem(product.getId(), product.getName(), product.getPrice(), product.getImageName());
                sessionCart.add(item);
            }
        }
    }

    public void updateQuantity(HttpSession session, Authentication authentication, Long productId, int quantity) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Cart cart = cartRepository.findByUserEmailAndProductId(email, productId);
            if (cart != null) {
                if (quantity <= 0) {
                    cartRepository.delete(cart);
                } else {
                    cart.setQuantity(quantity);
                    cartRepository.save(cart);
                }
            }
        } else {
            List<CartItem> sessionCart = (List<CartItem>) session.getAttribute(SESSION_CART_KEY);
            if (sessionCart != null) {
                sessionCart.removeIf(item -> item.getProductId().equals(productId) && quantity <= 0);
                sessionCart.forEach(item -> {
                    if (item.getProductId().equals(productId)) {
                        item.setQuantity(quantity);
                    }
                });
            }
        }
    }

    public void removeFromCart(HttpSession session, Authentication authentication, Long productId) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Cart cart = cartRepository.findByUserEmailAndProductId(email, productId);
            if (cart != null) {
                cartRepository.delete(cart);
            }
        } else {
            List<CartItem> sessionCart = (List<CartItem>) session.getAttribute(SESSION_CART_KEY);
            if (sessionCart != null) {
                sessionCart.removeIf(item -> item.getProductId().equals(productId));
            }
        }
    }

    public double getTotalPrice(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToDouble(CartItem::getTotal)
                .sum();
    }

    public int getCartCount(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public void clearCart(HttpSession session, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            List<Cart> userCart = cartRepository.findByUserEmail(email);
            cartRepository.deleteAll(userCart);
        } else {
            session.removeAttribute(SESSION_CART_KEY);
        }
    }

    private CartItem convertToCartItem(Cart cart) {
        CartItem item = new CartItem();
        item.setProductId(cart.getProductId());
        item.setProductName(cart.getProductName());
        item.setPrice(cart.getPrice());
        item.setImageName(cart.getImageName());
        item.setQuantity(cart.getQuantity());
        return item;
    }
}