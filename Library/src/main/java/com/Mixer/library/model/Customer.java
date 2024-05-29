package com.Mixer.library.model;

import com.Mixer.library.enums.AuthenticationType;
import com.twilio.rest.pricing.v1.voice.Country;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@EqualsAndHashCode
@Table(name = "Customers", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Customer implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private long id;
    private String firstName;
    private String  lastName;
    private String email;
    private String password;
    private String mobileNumber;
    private String roles;
    @Column(name = "is_activated")
    private boolean isActivated;


    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    private static final long OTP_VALID_DURATION = 5 * 60 * 1000;
    @Column(name = "one_time_password")
    private String otp;

    @Column(name = "otp_requested_time")
    private LocalDateTime otpRequestedTime;


    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Address> address;


    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    private ShoppingCart cart;


    @OneToOne(mappedBy = "customer")
    private Wallet wallet;


    @OneToMany(mappedBy = "customer")
    private List<Wishlist> wishlists;


    @Column(name="referalToken", unique = true)
    private String referalToken;



    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_type", length = 10)
    private AuthenticationType authenticationType;


    public String getFullName() {
        return firstName + " " + lastName;
    }

    private Country country;

}



