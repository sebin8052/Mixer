package com.Mixer.admin.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



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

    @GetMapping("/login-page")
    public String showLoginPage()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null||authentication instanceof AnonymousAuthenticationToken){
            return "login-page";
        }
        else
        {
            return "redirect:/index";
        }
    }
}
