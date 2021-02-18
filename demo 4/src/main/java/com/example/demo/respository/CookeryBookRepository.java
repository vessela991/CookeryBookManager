package com.example.demo.respository;

import com.example.demo.model.cookeryBook.CookeryBook;
import com.example.demo.model.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CookeryBookRepository extends CrudRepository<CookeryBook, Long> {
    CookeryBook findByName(String name);
}
