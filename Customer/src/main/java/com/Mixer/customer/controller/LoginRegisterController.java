package com.Mixer.customer.controller;


import com.Mixer.library.dto.CustomerDto;
import com.Mixer.library.model.Customer;
import com.Mixer.library.model.UserOtp;
import com.Mixer.library.repository.CustomerRepository;
import com.Mixer.library.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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


    private SmsService smsService;

    public LoginRegisterController(CustomerService customerService, CustomerRepository customerRepository, OtpService otpService, UserOtpService userOtpService, PasswordEncoder passwordEncoder, EmailService emailService, SmsService smsService) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.otpService = otpService;
        this.userOtpService = userOtpService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.smsService = smsService;
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




        @PostMapping("/do-register")
        public String registerCustomer(@Valid @ModelAttribute("customerDto") CustomerDto customerDto,
                                       BindingResult result,
                                       Model model,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes,HttpSession httpSession) {
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

                customerService.save(customerDto);

                String otps = smsService.generateOtp();
                smsService.sendOtp(otps);
                httpSession.setAttribute("customerDto",customerDto);
                httpSession.setAttribute("otp",otps);

                String otp = otpService.generateOTP();


                UserOtp userOTP = new UserOtp();
                userOTP.setEmail(username);
                userOTP.setOneTimePassword(passwordEncoder.encode(otp));
                userOTP.setCreatedAt(new Date());
                userOTP.setOtpRequestedTime(new Date());
                userOTP.setUpdateOn(new Date());
                userOtpService.saveOrUpdate(userOTP);


                String status = emailService.sendSimpleMail(username, otp);
                if (status.equals("success")) {
                    session.setAttribute("email", username);
                    redirectAttributes.addFlashAttribute("email", username);
                    return "redirect:/OtpValidation";

                }
                else {
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