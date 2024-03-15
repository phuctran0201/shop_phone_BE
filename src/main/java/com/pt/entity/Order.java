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
    @Field("orderItems")
    private List<OrderItem> orderItems;
    @Field("shippingAddress")
    private ShippingAddress shippingAddress;
    @Field("paymentMethod")
    private String paymentMethod;
    @Field("itemsPrice")
    private double itemsPrice;
    @Field("shippingPrice")
    private double shippingPrice;
    @Field("totalPrice")
    private double totalPrice;
    @Field("user")
    private User user;
    @Field("isPaid")
    private boolean isPaid;
    @Field("paidAt")
    private Date paidAt;
    @Field("isDelivered")
    private boolean isDelivered;
    @Field("deliveredAt")
    private Date deliveredAt;


}

