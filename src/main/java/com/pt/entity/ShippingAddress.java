package com.pt.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "shippingAddress")
public class ShippingAddress {

    private String id;

    private String address;

    private String city;

    private Integer phone;

}
