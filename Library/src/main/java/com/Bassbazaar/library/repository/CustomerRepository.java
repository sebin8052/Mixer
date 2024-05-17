package com.Bassbazaar.library.repository;

import com.Bassbazaar.library.enums.AuthenticationType;
import com.Bassbazaar.library.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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


        /* Check for duplicate name */
        boolean existsByFirstName(String firstName);


                 /*  google authentication*/

    @Query("UPDATE Customer c SET c.authenticationType = ?2 WHERE c.id = ?1")
    @Modifying
    public void updateAuthenticationType(Long customerId, AuthenticationType type);

}
