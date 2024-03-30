package com.Bassbazaar.library.service;

import com.Bassbazaar.library.Exception.CustomerNotFoundException;
import com.Bassbazaar.library.dto.CustomerDto;
import com.Bassbazaar.library.model.Customer;


import java.util.List;

public interface CustomerService
{
    Customer findByEmail(String email);


    Customer save(CustomerDto customerDto);

    List<Customer> findAll();

    Customer findById(long id);

    void disable(long id);

    void enable(long id);

    Customer update(CustomerDto customerDto);

  /*  CustomerDto findByEmailCustomerDto(String email); */// -> usercontroller

   /* CustomerDto updateAccount(CustomerDto customerDto,String email);*/ //-> usercontroller

    void changePass(CustomerDto customerDto);
    void updateResetPasswordToken(String token, String email) throws CustomerNotFoundException;
    Customer getByResetPasswordToken(String token);
    void updatePassword(Customer customer, String newPassword);
}
