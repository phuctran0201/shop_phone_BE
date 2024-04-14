package com.pt.req;

import com.pt.entity.OrderItem;
import com.pt.entity.ShippingAddress;
import com.pt.entity.User;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
public class CreateOrderRequest {
    private List<OrderItem> orderItems;

    private String paymentMethod;

    private Double itemsPrice;

    private Double shippingPrice;

    private Double totalPrice;

    private ShippingAddress shippingAddress;

    private String email;

    private Boolean isPaid;

    private Date paidAt;

    private String user;
}
