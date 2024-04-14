package com.pt.req;

import com.pt.entity.OrderItem;
import lombok.Data;

import java.util.List;

@Data
public class DeleteOrderRequest {

    private String Id;

    private List<OrderItem> orderItems;

    private String user;

}
