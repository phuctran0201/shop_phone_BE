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

    private Double price;

    private Integer countInStock;

    private Double rating;

    private String description;

    private Double discount;

    private Integer sold;

    private String createdAt;

    private String updatedAt;


}

