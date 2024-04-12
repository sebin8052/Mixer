package com.Bassbazaar.library.repository;

import com.Bassbazaar.library.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>
{
    Customer findByEmail(String email);

    Customer findById(long id);

    public Customer findByResetPasswordToken(String token);

    /* Referal */
    List<Customer> findByReferalToken(String token);
}
