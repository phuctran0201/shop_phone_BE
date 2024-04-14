package com.pt.controller;

import com.pt.req.CreateOrderRequest;
import com.pt.req.CreateProductRequest;
import com.pt.req.DeleteOrderRequest;
import com.pt.service.OrderService;
import com.pt.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(value = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest createOrderRequest) throws Exception {
        return ResponseEntity.ok(this.orderService.createOrder(createOrderRequest));
    }
    @GetMapping()
    public ResponseEntity<?> getAllOrder() throws Exception {
        return ResponseEntity.ok(this.orderService.getAllOrder());
    }
    @GetMapping(path = "/getAllOrder/{id}")
    public ResponseEntity<?> getAllOrderDetails( @PathVariable String id) throws Exception {
        return ResponseEntity.ok( this.orderService.getAllOrderDetails(id));
    }
    @GetMapping(path = "/getDetailsOrder/{id}")
    public ResponseEntity<?> getOrderDetails( @PathVariable String id) throws Exception {
        return ResponseEntity.ok( this.orderService.getOrderDetails(id));
    }
    @PostMapping(path = "/cancelOrder")
    public ResponseEntity<?> cancelOrder(@RequestBody DeleteOrderRequest deleteOrderRequest) throws Exception {
        return ResponseEntity.ok( this.orderService.cancelOrderDetails(deleteOrderRequest));
    }
}
