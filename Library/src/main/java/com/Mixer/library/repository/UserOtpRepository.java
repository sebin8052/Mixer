package com.Mixer.library.repository;

import com.Mixer.library.model.UserOtp;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOtpRepository extends JpaRepository<UserOtp,Long>
{
    boolean existsByEmail(String email);
    UserOtp findByEmail(String email);





}