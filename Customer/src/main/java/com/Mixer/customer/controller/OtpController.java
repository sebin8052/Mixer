package com.Mixer.customer.controller;

import com.Mixer.library.model.UserOtp;
import com.Mixer.library.service.CustomerService;
import com.Mixer.library.service.EmailService;
import com.Mixer.library.service.OtpService;
import com.Mixer.library.service.UserOtpService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

@Controller
public class OtpController
{
    @Autowired
    private OtpService otpService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserOtpService userOTPService;
    @Autowired
    private CustomerService usersSevice;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/verifyEmail")                                                                                   // ---> (4)
    public String showVerifyEmail()
    {
        return "verifyEmail";
    }








    @GetMapping("/OtpValidation")
    public String showOtpValidationPage(Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/register";
        }
        UserOtp userOTP = new UserOtp();
        userOTP.setEmail(email);
        model.addAttribute("userOTP", userOTP);
        return "OtpValidation";
    }



    @PostMapping("/validateOTP")
    public String validateOTP(@ModelAttribute("userOTP") UserOtp userOTPRequest, HttpSession session,
                              RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("email");
        String sessionOtp = (String) session.getAttribute("mobileOtp");

        if (email == null || sessionOtp == null) {
            return "redirect:/register";
        }


        UserOtp userOTP = userOTPService.findByEmail(email);
        boolean isEmailOtpValid = userOTP != null && passwordEncoder.matches(userOTPRequest.getOneTimePassword(), userOTP.getOneTimePassword());


        boolean isMobileOtpValid = userOTPRequest.getOneTimePassword().equals(sessionOtp);

        if (isEmailOtpValid || isMobileOtpValid) {

            session.removeAttribute("email");
            session.removeAttribute("mobileOtp");

            redirectAttributes.addFlashAttribute("success", "Registration successful!");
            return "redirect:/index";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid OTP, please try again.");
            return "redirect:/OtpValidation";
        }
    }

}
