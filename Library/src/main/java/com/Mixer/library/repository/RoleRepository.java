package com.Mixer.library.repository;

import com.Mixer.library.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository  extends JpaRepository<Role,Long>
{
  Role findByName(String name);
}
