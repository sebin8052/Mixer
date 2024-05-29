package com.Mixer.library.service.impl;

import com.Mixer.library.Exception.OtpSendException;
import com.Mixer.library.service.SmsService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Random;

@Service
public class SmsServiceImpl implements SmsService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;

    @PostConstruct
    public void initTwilio() {
        if (accountSid == null || authToken == null) {
            throw new OtpSendException("Twilio account SID or auth token is null");
        }
        Twilio.init(accountSid, authToken);
    }

    @Override
    public String generateOtp() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }

    @Override
    public void sendOtp(String otp) {
        try {
            PhoneNumber to = new PhoneNumber("+917012249327");
            PhoneNumber from = new PhoneNumber(fromPhoneNumber);
            String otpMessage = "Dear Customer, Your OTP is " + otp + " for sending SMS through Spring boot application. Thank You.";
            Message message = Message.creator(to, from, otpMessage).create();
        } catch (Exception e) {
            throw new OtpSendException("Error sending OTP: " + e.getMessage());
        }
    }
}
