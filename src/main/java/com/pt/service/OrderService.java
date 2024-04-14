package com.pt.service;

import com.pt.req.CreateOrderRequest;
import com.pt.req.DeleteOrderRequest;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    public ResponseEntity<?> createOrder(CreateOrderRequest createOrderRequest)throws Exception;

    public  ResponseEntity<?> getAllOrderDetails(String id) throws Exception;

    public  ResponseEntity<?> getOrderDetails(String id) throws Exception;

    public  ResponseEntity<?> cancelOrderDetails(DeleteOrderRequest deleteOrderRequest) throws Exception;

    public  ResponseEntity<?> getAllOrder () throws Exception;


}
