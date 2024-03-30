package com.Bassbazaar.admin.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashBoardController
{
    @GetMapping("/")
    public String getIndex(HttpSession session) {
        return "index";
    }
}
