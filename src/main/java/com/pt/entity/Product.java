package com.pt.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String name;
    private String image;
    private String type;
    private double price;
    private int countInStock;
    private double rating;
    private String description;
    private double discount;
    private int sold;
    private Date createdAt;
    private Date updatedAt;


}

