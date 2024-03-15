package com.pt.configSecurity.jwt;

import com.pt.configSecurity.SecurityService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ConfigJwtUtils  {

    private final SecurityService securityService;



    private final long JWT_EXPIRATION = 604800000L;

    public ConfigJwtUtils(SecurityService securityService) {
        this.securityService = securityService;
    }


    public String generateToken(String email,String id) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        String jwt= Jwts.builder()
                .setSubject(email).claim("id",id)
                .setIssuedAt(new Date())
                .setExpiration(new Date((   new Date()).getTime()+1*45*1000))
                .signWith(SignatureAlgorithm.HS512,"railway12SecretKey")
                .compact();


        return jwt;
    }

    public String getRoleFromJWT() {
        String role=null;
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName();
                UserDetails userDetails = securityService.loadUserByUsername(email);
                role = userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","));
            } catch (ExpiredJwtException e) {
                System.out.println("Expired JWT: " + e.getMessage());
            } catch (MalformedJwtException | SignatureException e) {
                System.out.println("Invalid JWT: " + e.getMessage());
            } catch (UsernameNotFoundException e) {
                System.out.println("User not found: " + e.getMessage());
            }


        return role;
    }


    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey("").parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }


}
