package com.ecommerce.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.dto.ProductDt;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.UserService;

@Controller
public class AdminController {

    @Autowired
    private CategoryService cservice;

    @Autowired
    private ProductService pservice;

    @Autowired
    private UserService userService;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/admin")
    public String adminRoot() {
        return "redirect:/admin/home";
    }

    @GetMapping("/admin/home")
    public String adminHome(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            model.addAttribute("adminEmail", email);

            Optional<User> adminOpt = userService.findByEmail(email);
            if (adminOpt.isPresent()) {
                model.addAttribute("adminUser", adminOpt.get());
            }
        }
        return "admin";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam("email") String email,
                           @RequestParam("password") String password) {
        User u = new User();
        u.setEmail(email);
        u.setPassword(password);
        u.setRole("ROLE_USER");
        userService.save(u);
        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/admin/categories")
    public String categoryPage(Model model) {
        List<Category> list = cservice.getAll();
        model.addAttribute("categories", list);
        return "categories";
    }

    @GetMapping("/admin/categories/add")
    public String addCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "categoriesAdd";
    }

    @PostMapping("/admin/categories/add")
    public String postAddCategory(@ModelAttribute("category") Category c) {
        cservice.saveCategory(c);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") int id) {
        cservice.deletebyId(id);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/categories/update/{id}")
    public String updateCategory(@PathVariable("id") int id, Model model) {
        Optional<Category> category = cservice.fetchbyId(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
            return "categoriesAdd";
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/products")
    public String productPage(Model model) {
        List<Product> list = pservice.getAll();
        model.addAttribute("products", list);
        return "products";
    }

    @GetMapping("/admin/products/add")
    public String addProductForm(Model model) {
        ProductDt p = new ProductDt();
        model.addAttribute("productDTO", p);
        model.addAttribute("categories", cservice.getAll());
        return "productsAdd";
    }

    @PostMapping("/admin/products/add")
    public String postAddProduct(@ModelAttribute("productDTO") ProductDt p,
                                 @RequestParam("productImage") MultipartFile file,
                                 @RequestParam("imgName") String imgName,
                                 Model model) throws IOException { 

        Product pro = new Product();
        pro.setId(p.getId());
        pro.setName(p.getName());
        pro.setPrice(p.getPrice());
        pro.setDescription(p.getDescription());
        pro.setWeight(p.getWeight());

        Optional<Category> categoryOpt = cservice.fetchbyId(p.getCategoryId());
        if (categoryOpt.isPresent()) {
            pro.setCategory(categoryOpt.get());
        } else {
            model.addAttribute("error", "Selected category not found");
            model.addAttribute("categories", cservice.getAll());
            model.addAttribute("productDTO", p);  
            return "productsAdd"; 
        }

        String imageUUID = imgName;
        if (!file.isEmpty()) {
            imageUUID = file.getOriginalFilename();
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            Path path = Paths.get(uploadPath, imageUUID);
            Files.write(path, file.getBytes());
        }
        pro.setImageName(imageUUID);
        pservice.saveProduct(pro);
        return "redirect:/admin/products";
    }

    @GetMapping("/admin/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") long id) {
        pservice.deletebyId(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/admin/products/update/{id}")
    public String updateProduct(@PathVariable("id") long id, Model model) {
        Optional<Product> proOpt = pservice.fetchbyId(id);
        if (proOpt.isPresent()) {
            Product pro = proOpt.get();
            ProductDt pdt = new ProductDt();
            pdt.setId(pro.getId());
            pdt.setName(pro.getName());
            pdt.setPrice(pro.getPrice());
            pdt.setWeight(pro.getWeight());
            pdt.setDescription(pro.getDescription());
            pdt.setCategoryId(pro.getCategory().getId());
            pdt.setImageName(pro.getImageName());

            model.addAttribute("productDTO", pdt);
            model.addAttribute("categories", cservice.getAll());
            return "productsAdd";
        }
        return "redirect:/admin/products";
    }
}