package com.pt.configSecurity;

import com.pt.entity.User;
import com.pt.enums.UserAuth;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {

    private String id;

    private String name;

    private String password;

    private String email;

    private UserAuth userAuth;



    public UserDetailsImpl(User user) {


        this.id=user.getId();
        this.name=user.getName();
        this.password=user.getPassword();
        this.email=user.getEmail();
        this.userAuth=user.getUserAuth();


    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userAuth.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
