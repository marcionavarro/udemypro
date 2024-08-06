package com.mn.pdv.repository;

import com.mn.pdv.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
   @Query("SELECT u FROM User u LEFT JOIN FETCH u.sales WHERE u.username = :username")
    User findUserByUsername(@Param("username") String username);
}
