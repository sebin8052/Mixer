package com.Bassbazaar.library.service.impl;


import com.Bassbazaar.library.Exception.CustomerNotFoundException;
import com.Bassbazaar.library.dto.CustomerDto;
import com.Bassbazaar.library.model.Customer;
import com.Bassbazaar.library.repository.CustomerRepository;
import com.Bassbazaar.library.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService
{
    @Autowired
    private CustomerRepository customerRepository;



    @Override
    public Customer findByEmail(String email)
    {

        return customerRepository.findByEmail(email);
    }

    @Override
    public Customer save(CustomerDto customerDto)
    {
        Customer customer = new Customer();
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setMobileNumber(customerDto.getMobileNumber());
        customer.setActivated(true);
        customer.setPassword((customerDto.getPassword()));
        customer.setEmail(customerDto.getEmail());
        customer.setRoles("User");
        return customerRepository.save(customer);
    }

    @Override
    public List<Customer> findAll()
    {
        return customerRepository.findAll();
    }

    @Override
    public Customer findById(long id) {
        return customerRepository.findById(id);
    }

    @Override
    public void disable(long id) {
        Customer customer=findById(id);
        customer.setActivated(false);
        customerRepository.save(customer);

    }

    @Override
    public void enable(long id) {
        Customer customer = findById(id);
        customer.setActivated(true);
        customerRepository.save(customer);
    }

    @Override
    public Customer update(CustomerDto customerDto) {
        Customer customer=customerRepository.findByEmail(customerDto.getEmail());
        customer.setPassword(customerDto.getPassword());
        return customerRepository.save(customer);
    }

    @Override
    public void changePass(CustomerDto customerDto) {
        Customer customer=customerRepository.findByEmail(customerDto.getEmail());
        customer.setPassword(customerDto.getPassword());
        customerRepository.save(customer);
    }

    @Override
    public void updateResetPasswordToken(String token, String email) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByEmail(email);
        if (customer != null) {
            customer.setResetPasswordToken(token);
            customerRepository.save(customer);
        } else {
            throw new CustomerNotFoundException("Could not find any customer with the email " + email);
        }
    }

    @Override
    public Customer getByResetPasswordToken(String token)
    {
     return customerRepository.findByResetPasswordToken(token);

    }


    public void updatePassword(Customer customer,String newPassword)
    {
        customer.setPassword(newPassword);
        customer.setResetPasswordToken(null);
        customerRepository.save(customer);
    }



}
