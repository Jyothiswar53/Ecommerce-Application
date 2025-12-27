package com.ecommerce.controller;

import com.ecommerce.model.Order;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class PageController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/shop")
    public String shop(Model model) {
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("products", productService.getAll());
        return "shop";
    }

    @GetMapping("/shop/category/{id}")
    public String shopByCategory(@PathVariable("id") int id, Model model) {
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("products", productService.getProByCatId(id));
        return "shop";
    }

    @GetMapping("/shop/viewproduct/{id}")
    public String viewProduct(@PathVariable("id") int id, Model model) {
        productService.fetchbyId(id).ifPresent(product -> {
            model.addAttribute("product", product);
        });
        return "viewProduct";
    }

    @GetMapping("/userform")
    public String userForm(Model model, Authentication authentication,
            @RequestParam(value = "productId", required = false) Integer productId,
            @RequestParam(value = "productName", required = false) String productName) {

        if (authentication == null || !authentication.isAuthenticated()) {
            String redirectUrl = "/userform";
            if (productId != null)
                redirectUrl += "?productId=" + productId;
            if (productName != null)
                redirectUrl += (productId != null ? "&" : "?") + "productName=" + productName;
            return "redirect:/login?returnUrl=" + redirectUrl;
        }

        model.addAttribute("order", new Order());
        model.addAttribute("productId", productId != null ? productId : 0);
        model.addAttribute("productName", productName != null ? productName : "");
        return "userform";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(
            @Valid @ModelAttribute("order") Order order,
            BindingResult result,
            @RequestParam(value = "productId", required = false, defaultValue = "0") int productId,
            @RequestParam(value = "productName", required = false, defaultValue = "") String productName,
            Authentication authentication,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("productId", productId);
            model.addAttribute("productName", productName);
            return "userform";
        }

        order.setEmail(authentication.getName());
        order.setProductId(productId);
        order.setProductName(productName);
        order.setOrderDate(LocalDateTime.now());

        orderService.saveOrder(order);

        return "redirect:/orderConfirmation";
    }

    @GetMapping("/orderConfirmation")
    public String orderConfirmation() {
        return "orderConfirmation";
    }

    @GetMapping("/orders")
    public String viewOrders(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String userEmail = authentication.getName();
        List<Order> orders = orderService.getOrdersByEmail(userEmail);

        if (orders == null) {
            orders = List.of();
        }

        model.addAttribute("orders", orders);
        return "orders";
    }
}