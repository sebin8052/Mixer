package com.Bassbazaar.customer.controller;

import com.Bassbazaar.library.model.UserOtp;
import com.Bassbazaar.library.service.CustomerService;
import com.Bassbazaar.library.service.EmailService;
import com.Bassbazaar.library.service.OtpService;
import com.Bassbazaar.library.service.UserOtpService;
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

    @GetMapping("/OtpValidation")                                                                               // ----------->(6)
    public String showotpvalidationPage(Model model, HttpSession session)
    {
        String email = (String) model.asMap().get("email");
        UserOtp userOTP = new UserOtp();
        userOTP.setEmail(email);
        session.setAttribute("email",email);
        model.addAttribute("userOTP",userOTP);
        return "OtpValidation";
    }


    @PostMapping("/sendVerificationEmailOtp")                                                                     // ------> (5)
    public String sendVerificationEmailOtp(
            @RequestParam("email")String email,
             HttpSession session,
            RedirectAttributes redirectAttributes) throws Exception
    {
        if(userOTPService.findByEmail(email)==null)
        {
            String otp = otpService.generateOTP();
            if(!userOTPService.existsByEmail(email))
            {
                UserOtp userOTP =new UserOtp();
                userOTP.setEmail(email);
                userOTP.setOneTimePassword(passwordEncoder.encode(otp));
                userOTP.setCreatedAt(new Date());
                userOTP.setOtpRequestedTime(new Date());
                userOTP.setUpdateOn(new Date());
                try{
                    userOTPService.saveOrUpdate(userOTP);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    throw new Exception("Couldn't finish OTP verification process"+ HttpStatus.BAD_REQUEST);
                }
            }else
            {
                UserOtp userOTP=userOTPService.findByEmail(email);              //exist un database
                userOTP.setOneTimePassword(passwordEncoder.encode(otp));
                userOTP.setOtpRequestedTime(new Date());
                userOTP.setUpdateOn(new Date());
                try{
                    userOTPService.saveOrUpdate(userOTP);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    throw new Exception("Couldn't finish OTP verification process");
                }
            }

            //email sending [sucess]
            String status = emailService.sendSimpleMail(email,otp);
            if(status.equals("success")){
                session.setAttribute("message","otpsent");
                redirectAttributes.addFlashAttribute("email",email);
                return "redirect:/OtpValidation";

            }
            else
            {
                return "redirect:/verifyEmail?error";
            }
        }


        else{
            return "redirect:/verifyEmail?existUser";
        }
    }


    /* Main otp method */


    @PostMapping("/validateOTP")
    public String validateOTP(@ModelAttribute("userOTP") UserOtp userOTPRequest, HttpSession session,
                              RedirectAttributes redirectAttributes)
    {
        String email=session.getAttribute("email").toString();
        UserOtp userOTP = userOTPService.findByEmail(userOTPRequest.getEmail());
        if(passwordEncoder.matches(userOTPRequest.getOneTimePassword(),userOTP.getOneTimePassword()))
        {
            //navigate to signup page
            redirectAttributes.addFlashAttribute("email",userOTP.getEmail());
            return "/index";
        }
        else
        {
            return "redirect:/OtpValidation?error";
        }
    }



    /*Resent otp controller */

/*    @PostMapping("/resendVerificationEmailOtp")
    public String resendVerificationEmailOtp(@RequestParam("email")String email,
                                             HttpSession session,
                                             RedirectAttributes redirectAttributes)   throws Exception
    {


    }*/


/* chatgtp prompt */

/*    @PostMapping("/validateOTP")
    public String validateOTP(@ModelAttribute("userOTP") UserOtp userOTPRequest, HttpSession session,
                              RedirectAttributes redirectAttributes) {
        String email = session.getAttribute("email").toString();

        UserOtp userOTP = userOTPService.findByEmail(userOTPRequest.getEmail());
        long intervalInSeconds = 5 * 60;

        // Check if userOTP is null to avoid NullPointerException
        if (userOTP == null) {
            // Handle case where userOTP is not found
            return "redirect:/OtpValidation?error=notfound";
        }

        // Convert Date to Instant
        Instant instant = userOTP.getOtpRequestedTime().toInstant();

        LocalTime otpRequestedTime = instant.atZone(ZoneId.systemDefault()).toLocalTime();

        LocalTime currentTime = LocalTime.now();

        LocalTime otpExpirationTime = otpRequestedTime.plusSeconds(intervalInSeconds);
        if (currentTime.isAfter(otpExpirationTime)) {
            // Handle case where OTP has expired
            return "redirect:/OtpValidation?error=expired";
        }

        if (passwordEncoder.matches(userOTPRequest.getOneTimePassword(), userOTP.getOneTimePassword())) {
            // Navigate to signup page
            redirectAttributes.addFlashAttribute("email", userOTP.getEmail());
            return "redirect:/index";
        } else {
            // Handle case where OTP is incorrect
            return "redirect:/OtpValidation?error=incorrect";
        }
    }*/



/*    testing method  for validate otp */

/*    @PostMapping("/validateOTP")
    public String validateOTP(@ModelAttribute("userOTP") UserOtp userOTPRequest, HttpSession session,
                              RedirectAttributes redirectAttributes){
        String email=session.getAttribute("email").toString();

        UserOtp userOTP = userOTPService.findByEmail(userOTPRequest.getEmail());
        long intervalInSeconds = 5 * 60;

// Convert Date to Instant
        Instant instant = userOTP.getOtpRequestedTime().toInstant();

        LocalTime otpRequestedTime = instant.atZone(ZoneId.systemDefault()).toLocalTime();

        LocalTime currentTime = LocalTime.now();

        LocalTime otpExpirationTime = otpRequestedTime.plusSeconds(intervalInSeconds);
        if (currentTime.isAfter(otpExpirationTime))
        {


        }
        if(passwordEncoder.matches(userOTPRequest.getOneTimePassword(),userOTP.getOneTimePassword())){
            //navigate to signup page
            redirectAttributes.addFlashAttribute("email",userOTP.getEmail());
            return "/index";
        }else{

            return "redirect:/OtpValidation?error";
        }
    }*/









 /*
 //     Reset the password with the help of email link
    @PostMapping("/sendEmailOTPLogin")
    public String sendEmailOTPLogin(
            @RequestParam("email")String email
            , HttpSession session,
            RedirectAttributes redirectAttributes) throws Exception {
        if(usersSevice.findByEmail(email)!=null){
            String otp = otpService.generateOTP();
            UserOtp userOTP = userOTPService.findByEmail(email);
            if(userOTP!=null){
                userOTP.setOneTimePassword(passwordEncoder.encode(otp));
                userOTP.setOtpRequestedTime(new Date());
                userOTP.setUpdateOn(new Date());
            }else{
                userOTP = new UserOtp();
                userOTP.setEmail(email);
                userOTP.setOneTimePassword(passwordEncoder.encode(otp));
                userOTP.setCreatedAt(new Date());
                userOTP.setOtpRequestedTime(new Date());
                userOTP.setUpdateOn(new Date());
            }
            try{
                userOTPService.saveOrUpdate(userOTP);
            }catch(Exception e){
                e.printStackTrace();
                throw new Exception("Send OTP.Please try after some time...");
            }
            String status = emailService.sendSimpleMail(email,otp);
            if(status.equals("success")){
                session.setAttribute("message","otpsent");
                redirectAttributes.addFlashAttribute("email",email);
                redirectAttributes.addAttribute("message","We have send a reset password link to your email. Please check your email");
                return "redirect:/forgotPasswordOTPLogin";

            }else{
                return "redirect:/forgot_password?error";
            }
        }else{
            return "redirect:/forgot_password?error";
        }
    }*/
}
