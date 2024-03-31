package com.pt.req;

import lombok.Data;

@Data
public class SignUp {
    private String name;

    private String email;

    private String password;

    private String confirmPassword;

    private String userAuth;

    private int phone;

    private String createdAt;

    private String updatedAt;
}
