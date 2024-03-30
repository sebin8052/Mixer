package com.Bassbazaar.admin.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//                    Admin login page controller

@Controller
public class LoginController
{

    @GetMapping("/login")
    public String getLoginForm(HttpSession session) {
        Object attribute = session.getAttribute("userLoggedIn");
        if (attribute != null) {
            return "redirect:/index";
        }
        return "login";
    }

    @GetMapping("/login-page")                    //check if the user is authenticated or not
    public String showLoginPage()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();   //retrives curremt authentication information
        if(authentication==null||authentication instanceof AnonymousAuthenticationToken){
            return "login-page";
        }
        else
        {
            return "redirect:/index";
        }
    }
}
