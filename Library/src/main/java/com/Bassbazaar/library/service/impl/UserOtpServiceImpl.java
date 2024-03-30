package com.Bassbazaar.library.service.impl;


import com.Bassbazaar.library.model.UserOtp;
import com.Bassbazaar.library.repository.UserOtpRepository;
import com.Bassbazaar.library.service.UserOtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserOtpServiceImpl implements UserOtpService
{
    @Autowired
    private UserOtpRepository userOTPRepository;
    @Override
    public void saveOrUpdate(UserOtp userOTP) {
        userOTPRepository.save(userOTP);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userOTPRepository.existsByEmail(email);
    }

    @Override
    public UserOtp findByEmail(String email) {
        return userOTPRepository.findByEmail(email);
    }



}