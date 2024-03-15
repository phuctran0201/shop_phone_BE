package com.pt.entity;

import com.pt.enums.UserAuth;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "users")
@Data
public class User {

    @Id
    private String id;

    private String name;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserAuth userAuth;

    private int phone;

    private String address;

    private String avatar;

    private String city;

    private String createdAt;

    private String updatedAt;
}