package com.pt.req;

import com.pt.enums.UserAuth;
import lombok.Data;

@Data
public class UpdateUserRequest {

    private String id;

    private String name;

    private String email;

    private String password;

    private UserAuth userAuth;

    private Integer phone;

    private String address;

    private String avatar;

    private String city;

    private String updatedAt;
}
