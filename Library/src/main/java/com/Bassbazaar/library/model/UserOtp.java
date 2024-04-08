package com.Bassbazaar.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="user_otp")
public class UserOtp
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String email;

    private static final long OTP_VALID_DURATION = 5 * 60 * 1000;
    private String oneTimePassword;

    private Date otpRequestedTime;
    private Date createdAt;
    private Date updateOn;


}
