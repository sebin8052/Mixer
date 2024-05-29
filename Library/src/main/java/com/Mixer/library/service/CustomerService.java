package com.Mixer.library.service;

import com.Mixer.library.Exception.CustomerNotFoundException;
import com.Mixer.library.dto.CustomerDto;
import com.Mixer.library.enums.AuthenticationType;
import com.Mixer.library.model.Customer;


import java.util.List;
import java.util.Optional;

public interface CustomerService
{
    Customer findByEmail(String email);


    Customer save(CustomerDto customerDto);

    List<Customer> findAll();

    Customer findById(long id);

    void disable(long id);

    void enable(long id);

    Customer update(CustomerDto customerDto);

    CustomerDto findByEmailCustomerDto(String email);

    CustomerDto updateAccount(CustomerDto customerDto,String email);

    void changePass(CustomerDto customerDto);
    void updateResetPasswordToken(String token, String email) throws CustomerNotFoundException;
    Customer getByResetPasswordToken(String token);
    void updatePassword(Customer customer, String newPassword);


    Optional<List<Customer>> getByReferalToken(String token);
    void updateReferalCodeToken(String token,String email);


    boolean existsByFirstName(String firstName);


    void updateAuthenticationType(Customer customer ,AuthenticationType type);

    void addNewCustomerUponOAuthLogin(String name, String email);
}
