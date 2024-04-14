package com.pt.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class ShippingAddress {

    private String fullName;

    private String address;

    private String city;

    private Integer phone;


}
