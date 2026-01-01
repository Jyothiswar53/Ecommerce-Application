package com.ecommerce.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecommerce.dto.ProductDt;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Controller
public class AdminController {

    @Autowired
    private CategoryService cservice;

    @Autowired
    private ProductService pservice;

    @Autowired
    private UserService userService;

    @Autowired
    private Cloudinary cloudinary;

    @GetMapping("/admin/products/add")
    public String addProductForm(Model model) {
        model.addAttribute("productDTO", new ProductDt());
        model.addAttribute("categories", cservice.getAll());
        return "productsAdd";
    }

    @PostMapping("/admin/products/add")
    public String postAddProduct(
            @ModelAttribute("productDTO") ProductDt p,
            @RequestParam("productImage") MultipartFile file) throws IOException {

        Product pro = new Product();
        pro.setId(p.getId());
        pro.setName(p.getName());
        pro.setPrice(p.getPrice());
        pro.setDescription(p.getDescription());
        pro.setWeight(p.getWeight());

        Category category = cservice.fetchbyId(p.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        pro.setCategory(category);

        if (!file.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "ecommerce/products")
            );
            pro.setImageName(uploadResult.get("secure_url").toString());
        }

        pservice.saveProduct(pro);
        return "redirect:/admin/products";
    }
}
