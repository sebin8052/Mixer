package com.Bassbazaar.library.repository;

import com.Bassbazaar.library.model.UserOtp;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOtpRepository extends JpaRepository<UserOtp,Long>
{
    boolean existsByEmail(String email);
    UserOtp findByEmail(String email);



/*    @Transactional
    void deleteByEmail(String email);*/


}