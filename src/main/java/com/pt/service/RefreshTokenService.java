package com.pt.service;

import com.pt.entity.RefreshToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface RefreshTokenService {
    public Optional<RefreshToken> findByToken(String token);

    public RefreshToken createRefreshToken(String email);

    public RefreshToken verifyExpiration(RefreshToken token);

    public int deleteByUserId(String userId);
}
