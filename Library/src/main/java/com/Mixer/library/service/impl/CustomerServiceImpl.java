package com.Mixer.library.service.impl;


import com.Mixer.library.Exception.CustomerNameAlreadyExistsException;
import com.Mixer.library.Exception.CustomerNotFoundException;
import com.Mixer.library.dto.CustomerDto;
import com.Mixer.library.enums.AuthenticationType;
import com.Mixer.library.model.Customer;
import com.Mixer.library.repository.CustomerRepository;
import com.Mixer.library.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService
{
    @Autowired
    private CustomerRepository customerRepository;


    public CustomerServiceImpl(CustomerRepository customerRepository)
    {
        this.customerRepository = customerRepository;
    }


    @Override
    public Customer findByEmail(String email)
    {

        return customerRepository.findByEmail(email);
    }

    @Override
    public Customer save(CustomerDto customerDto)
    {
     String customername = customerDto.getFirstName();

     if(existsByFirstName(customername))
     {
         throw new CustomerNameAlreadyExistsException("The customer  name already registered!");
     }
        Customer customer = new Customer();
     try
     {


         customer.setFirstName(customerDto.getFirstName());
         customer.setLastName(customerDto.getLastName());
         customer.setMobileNumber(customerDto.getMobileNumber());
         customer.setActivated(true);
         customer.setPassword((customerDto.getPassword()));
         customer.setEmail(customerDto.getEmail());
         customer.setRoles("User");
         return customerRepository.save(customer);
     }
     catch (Exception e)
     {
    e.printStackTrace();
     return null;
     }
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
    public void changePass(CustomerDto customerDto)
    {
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



    @Override
    public CustomerDto findByEmailCustomerDto(String email) {
        Customer customer = customerRepository.findByEmail(email);
        CustomerDto customerDto=new CustomerDto();
        customerDto.setEmail(customer.getEmail());
        customerDto.setId(customer.getId());
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setMobileNumber(customer.getMobileNumber());
        customerDto.setAddress(customer.getAddress());
        customerDto.setPassword(customer.getPassword());
        customerDto.set_activated(customer.isActivated());
        return customerDto;
    }

    @Override
    public CustomerDto updateAccount(CustomerDto customerDto, String email) {
        Customer customer= findByEmail(email);
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setMobileNumber(customerDto.getMobileNumber());
        customerRepository.save(customer);

        CustomerDto customerDtoUpdated = convertEntityToDto(customer);
        return customerDtoUpdated;
    }


    public CustomerDto convertEntityToDto(Customer customer)
    {
        CustomerDto customerDto=new CustomerDto();

        customerDto.setId(customer.getId());
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setMobileNumber(customer.getMobileNumber());
        customerDto.set_activated(customer.isActivated());
        customerDto.setPassword(customer.getPassword());
        return customerDto;
    }



    @Override
    public void updateReferalCodeToken(String token, String email) {
        Customer customer=customerRepository.findByEmail(email);
        if(customer!=null){
            customer.setReferalToken(token);
            customerRepository.save(customer);
        }
    }


    @Override
    public Optional<List<Customer>> getByReferalToken(String token) {
        return Optional.ofNullable(customerRepository.findByReferalToken(token));
    }


    @Override
    public boolean existsByFirstName(String firstName)
    {
        return customerRepository.existsByFirstName(firstName);
    }



    /* Google method */
    @Override
    public void updateAuthenticationType(Customer customer, AuthenticationType type)
    {
        if(!customer.getAuthenticationType().equals(type))
        {
            customerRepository.updateAuthenticationType(customer.getId(),type);
        }
    }



    @Override
    public void addNewCustomerUponOAuthLogin(String name, String email)
    {
        Customer customer = new Customer();
        customer.setEmail(email);
        setName(name,customer);

        customer.setActivated(true);
        customer.setAuthenticationType(AuthenticationType.GOOGLE);
        customer.setMobileNumber("");
        customer.setPassword("");
        customerRepository.save(customer);
    }



    private void setName(String name, Customer customer) {
        String[] nameArray = name.split(" ");
        if (nameArray.length < 2) {
            customer.setFirstName(name);
            customer.setLastName("");
        } else {
            String firstName = nameArray[0];
            customer.setFirstName(firstName);

            String lastName = name.replaceFirst(firstName, "");
            customer.setLastName(lastName);
        }
    }

}
