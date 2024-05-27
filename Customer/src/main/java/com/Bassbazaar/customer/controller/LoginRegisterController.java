package com.Bassbazaar.customer.controller;

import com.Bassbazaar.library.Exception.CustomerNameAlreadyExistsException;
import com.Bassbazaar.library.dto.CustomerDto;
import com.Bassbazaar.library.model.Customer;
import com.Bassbazaar.library.model.UserOtp;
import com.Bassbazaar.library.repository.CustomerRepository;
import com.Bassbazaar.library.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Date;

@Controller
public class LoginRegisterController {


    private CustomerService customerService;

    private CustomerRepository customerRepository;

    private OtpService otpService;

    private UserOtpService userOtpService;

    private PasswordEncoder passwordEncoder;

    private EmailService emailService;

    public LoginRegisterController(CustomerService customerService, CustomerRepository customerRepository, OtpService otpService, UserOtpService userOtpService, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.otpService = otpService;
        this.userOtpService = userOtpService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }


    @GetMapping("/login")
    public String getLoginForm(Model model, HttpSession session) {
        model.addAttribute("title", "Login Page");
        Object attribute = session.getAttribute("userLoggedIn");
        if (attribute != null) {
            return "redirect:/index";
        }
        return "login";
    }


    @GetMapping("/register")
    public String getRegisterForm(Model model) {
        model.addAttribute("title", "Register");
        model.addAttribute("customerDto", new CustomerDto());
        return "register";
    }


/*    @PostMapping("/do-register")                                                                    // @valid :check the validation in cusotmerDto
    public String registerCustomer(@Valid @ModelAttribute("customerDto") CustomerDto customerDto,
                                   BindingResult result,
                                   Model model,HttpSession httpSession)
    {

        try
        {
            if (result.hasErrors())                                         //check any validation error in the customeDto
            {
                model.addAttribute("customerDto", customerDto);
                return "register";
            }

            String username = customerDto.getEmail();
            Customer existingCustomer = customerService.findByEmail(username);
            if (existingCustomer != null)
            {
                model.addAttribute("customerDto", customerDto);                                      //
                model.addAttribute("error", "This Email is already Registered!");
                return "register";
            }

            else
            {
                                                          // Save the customer only if it's not already registered
                customerService.save(customerDto);
                model.addAttribute("success", "Registration successful!");
                return "verifyEmail";
            }


        }
        catch(CustomerNameAlreadyExistsException e)
        {
            e.printStackTrace();
            model.addAttribute("error", "The customer name is already registered!");
            return "register";
        }
        catch (Exception e)
        {
            e.printStackTrace();
            model.addAttribute("error", "Server is error, try again later!");
            return "register";
        }
    }
}*/


        @PostMapping("/do-register")
        public String registerCustomer(@Valid @ModelAttribute("customerDto") CustomerDto customerDto,
                                       BindingResult result,
                                       Model model,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
            try {
                if (result.hasErrors()) {
                    model.addAttribute("customerDto", customerDto);
                    return "register";
                }

                String username = customerDto.getEmail();
                Customer existingCustomer = customerService.findByEmail(username);
                if (existingCustomer != null) {
                    model.addAttribute("customerDto", customerDto);
                    model.addAttribute("error", "This Email is already Registered!");
                    return "register";
                }

                customerService.save(customerDto); // Save the customer

                String otp = otpService.generateOTP(); // Generate OTP

                // Save OTP for verification
                UserOtp userOTP = new UserOtp();
                userOTP.setEmail(username);
                userOTP.setOneTimePassword(passwordEncoder.encode(otp));
                userOTP.setCreatedAt(new Date());
                userOTP.setOtpRequestedTime(new Date());
                userOTP.setUpdateOn(new Date());
                userOtpService.saveOrUpdate(userOTP);

                // Send OTP via email
                String status = emailService.sendSimpleMail(username, otp);
                if (status.equals("success")) {
                    session.setAttribute("email", username);
                    redirectAttributes.addFlashAttribute("email", username);
                    return "redirect:/OtpValidation";
                } else {
                    model.addAttribute("error", "Failed to send OTP, please try again.");
                    return "register";
                }

            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", "Server error, please try again later.");
                return "register";
            }
        }
}