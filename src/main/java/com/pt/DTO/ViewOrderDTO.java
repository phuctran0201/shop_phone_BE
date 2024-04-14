package com.pt.DTO;

import com.pt.entity.OrderItem;
import com.pt.entity.ShippingAddress;
import com.pt.entity.User;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ViewOrderDTO {
    private String id;

    private List<OrderItem> orderItems;

    private ShippingAddress shippingAddress;

    private String paymentMethod;

    private Double itemsPrice;

    private Double shippingPrice;

    private Double totalPrice;

    private String user;

    private Boolean isPaid;

    private String email;

    private Date paidAt;

    private boolean isDelivered;

    private Date deliveredAt;

    private String createdAt;

    private String updatedAt;
}
