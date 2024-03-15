package com.pt.repository;

import com.pt.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(String id);
    List<User> findAllByOrderByCreatedAtDescUpdatedAtDesc();
}
