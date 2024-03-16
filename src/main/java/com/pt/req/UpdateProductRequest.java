package com.pt.req;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateProductRequest {
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

    private String updatedAt;
}
