package com.pt.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@RequiredArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    private String id;

    private List<OrderItem> orderItems;

    private ShippingAddress shippingAddress;

    private String paymentMethod;

    private Double itemsPrice;

    private Double shippingPrice;

    private Double totalPrice;

    private String user;

    private String email;

    private Boolean isPaid;

    private Date paidAt;

    private Boolean isDelivered;

    private Date deliveredAt;

    private String createdAt;

    private String updatedAt;
}

