package com.Bassbazaar.customer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
public class CustomerDetails extends User {
    private final String firstName;
    private final String lastName;
    private final String mobileNumber;
    private final boolean activated;

    public CustomerDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
                           String firstName, String lastName, String mobileNumber, boolean activated) {
        super(username, password, authorities);
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.activated = activated;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public boolean isActivated() {
        return activated;
    }
}

