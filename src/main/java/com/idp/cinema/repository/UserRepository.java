package com.idp.cinema.repository;

import com.idp.cinema.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
    void deleteByUsername(String username);
}
