package com.pt.service;

import com.pt.entity.OrderItem;

import java.util.List;

public interface SendEmailService {
    public  void sendEmailCreateOrder(String email, List<OrderItem> orderItems) ;
}
