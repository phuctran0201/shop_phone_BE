package com.pt.DTO;

import com.pt.enums.UserAuth;
import lombok.Data;

@Data
public class ViewUserDTO {

    private String id;

    private String name;

    private String email;

    private String password;

    private UserAuth userAuth;

    private Integer phone;

    private String address;

    private String avatar;

    private String createdAt;

    private String updatedAt;
}
