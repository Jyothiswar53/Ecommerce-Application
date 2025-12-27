package com.ecommerce.controller;

import com.ecommerce.entity.Product;
import com.ecommerce.service.CartService;
import com.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable("id") Long id,
                            HttpSession session,
                            Authentication authentication) {
        Optional<Product> productOpt = productService.fetchbyId(id);
        if (productOpt.isPresent()) {
            Product p = productOpt.get();
            cartService.addToCart(session, authentication, p);
        }
        return "redirect:/shop";
    }

    @GetMapping("/cart")
    public String viewCart(Model model,
                           HttpSession session,
                           Authentication authentication) {
        List<com.ecommerce.dto.CartItem> cartItems = cartService.getCartItems(session, authentication);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", cartService.getTotalPrice(cartItems));
        model.addAttribute("cartCount", cartService.getCartCount(cartItems));
        return "cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam("productId") Long productId,
                             @RequestParam("quantity") int quantity,
                             HttpSession session,
                             Authentication authentication) {
        cartService.updateQuantity(session, authentication, productId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable("id") Long id,
                                 HttpSession session,
                                 Authentication authentication) {
        cartService.removeFromCart(session, authentication, id);
        return "redirect:/cart";
    }
}