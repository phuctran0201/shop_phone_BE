package com.pt.repository;

import com.pt.entity.RefreshToken;
import com.pt.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken,String> {
    Optional<RefreshToken> findByToken(String token);

    int deleteByUser(User user);
}
