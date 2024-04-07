package com.Bassbazaar.admin.controller;

import com.Bassbazaar.library.service.CategoryService;
import com.Bassbazaar.library.service.OrderService;
import com.Bassbazaar.library.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashBoardController
{

    private OrderService orderService;
    private ProductService productService;
    private CategoryService categoryService;

    public DashBoardController(OrderService orderService, ProductService productService, CategoryService categoryService) {
        this.orderService = orderService;
        this.productService = productService;
        this.categoryService = categoryService;
    }


    /* Editing required */
    @GetMapping("/")
    public String getIndex(HttpSession session) {
        return "index";
    }
}
