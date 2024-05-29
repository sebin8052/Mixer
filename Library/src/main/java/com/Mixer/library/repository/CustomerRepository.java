package com.Mixer.library.repository;

import com.Mixer.library.enums.AuthenticationType;
import com.Mixer.library.model.Customer;
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


    List<Customer> findByReferalToken(String token);



        boolean existsByFirstName(String firstName);


                 /*  google authentication*/

    @Query("UPDATE Customer c SET c.authenticationType = ?2 WHERE c.id = ?1")
    @Modifying
    public void updateAuthenticationType(Long customerId, AuthenticationType type);

}
