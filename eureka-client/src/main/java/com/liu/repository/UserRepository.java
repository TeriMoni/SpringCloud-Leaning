package com.liu.repository;

import com.liu.entity.JmlUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<JmlUser,Integer> {

    JmlUser findByPhoneNumber(String phoneNumber);

}
