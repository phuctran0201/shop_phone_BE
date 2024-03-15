package com.pt.req;

import lombok.Data;

@Data
public class CreateUserRequest {

    private String name;

    private String email;

    private String password;

    private String userAuth;

    private int phone;

    private String address;

    private String avatar;

    private String city;

    private String createdAt;

    private String updatedAt;
}
