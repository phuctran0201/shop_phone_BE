package com.pt.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;


@Data
public class OrderItem {

    private String name;

    private Integer amount;

    private String image;

    private Double price;

    private Double discount;

    private String product;


}
