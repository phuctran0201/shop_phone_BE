package com.pt.configSecurity.jwt;

import com.pt.configSecurity.SecurityService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RequestFilter extends OncePerRequestFilter {

    private final SecurityService securityService;

    public RequestFilter(SecurityService securityService) {
        this.securityService = securityService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token=request.getHeader("Authorization");
        if (token==null){

        }
        if (token != null && token.contains("Bearer ")){
            token=token.replace("Bearer ","");
            try {


                Jws<Claims> claimsJws = Jwts.parser().setSigningKey("railway12SecretKey").parseClaimsJws(token);
                String email = claimsJws.getBody().getSubject();
                UserDetails userDetails = securityService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            catch (ExpiredJwtException e){
                System.out.println(e.getMessage());
            }
        }

        filterChain.doFilter(request,response);

    }


}
