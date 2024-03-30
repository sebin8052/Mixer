package com.Bassbazaar.library.service;

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
}
