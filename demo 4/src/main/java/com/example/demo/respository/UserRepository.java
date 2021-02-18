package com.example.demo.respository;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.user.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
}
