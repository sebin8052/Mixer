package com.Bassbazaar.library.service;

import com.Bassbazaar.library.model.UserOtp;


public interface UserOtpService
{
    public void saveOrUpdate(UserOtp userOTP);
    public boolean existsByEmail(String email);
    public UserOtp findByEmail(String email);


//    public void deleteByEmail(String email);

}