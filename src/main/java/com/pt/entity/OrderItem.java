package com.pt.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "orderItem")
@Data
public class OrderItem {

    private String id;

    private Long amount;

    private String image;

    private double price;

    private double discount;
}
