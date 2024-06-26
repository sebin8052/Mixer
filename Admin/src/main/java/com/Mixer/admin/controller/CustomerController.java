package com.Mixer.admin.controller;


import com.Mixer.library.model.Customer;
import com.Mixer.library.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CustomerController
{

    private CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping("/customers")
    public String categories(Model model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
        {
            return "redirect:/login";
        }
        List<Customer> customers = customerService.findAll();
        model.addAttribute("customers", customers);
        model.addAttribute("size", customers.size());

        model.addAttribute("confirmationMessage","Are you sure you want to disable this  customers?");

        return "customers";
    }


    @GetMapping("/disable-customer/{id}")
    public String disable(@PathVariable("id")long id, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("success","Customer Disabled");

        customerService.disable(id);

        return "redirect:/customers";
    }

    @GetMapping("/enable-customer/{id}")
    public String enable(@PathVariable("id")long id, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("success","Customer Enabled");

        customerService.enable(id);

        return "redirect:/customers";
    }
}
