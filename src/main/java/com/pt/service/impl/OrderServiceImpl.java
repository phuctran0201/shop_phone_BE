package com.pt.service.impl;

import com.pt.DTO.ViewOrderDTO;
import com.pt.entity.Order;
import com.pt.entity.OrderItem;
import com.pt.entity.Product;
import com.pt.entity.User;
import com.pt.exceptionMessage.MessageResponse;
import com.pt.repository.OrderRepository;
import com.pt.repository.ProductRepository;
import com.pt.repository.UserRepository;
import com.pt.req.CreateOrderRequest;
import com.pt.req.DeleteOrderRequest;
import com.pt.service.OrderService;
import com.pt.service.SendEmailService;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final SendEmailService sendEmailService;

    public OrderServiceImpl(ProductRepository productRepository, OrderRepository orderRepository, UserRepository userRepository, ModelMapper modelMapper, SendEmailService sendEmailService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.sendEmailService = sendEmailService;
    }

    @Override
    public ResponseEntity<?> createOrder(CreateOrderRequest createOrderRequest) throws Exception {
        try {
            for (OrderItem orderItem : createOrderRequest.getOrderItems()) {
                Integer amount = orderItem.getAmount();
                String product = orderItem.getProduct();
                Optional<Product> productData = productRepository.findById(product);

                if (productData.isPresent()) {
                    Product existingProduct = productData.get();
                    Integer countInStockUpdate = existingProduct.getCountInStock() - amount;
                    Integer soldUpdate = existingProduct.getSold() + amount;
                    if (countInStockUpdate < 0) {
                        return ResponseEntity.badRequest().body("Product with ID: " + product + " is out of stock");
                    } else {
                        existingProduct.setSold(soldUpdate);
                        existingProduct.setCountInStock(countInStockUpdate);

                        productRepository.save(existingProduct);
                    }
                } else {
                    return ResponseEntity.badRequest().body("Product with ID: " + product + " does not exist");
                }
            }
            LocalDateTime current = LocalDateTime.now();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

            String formatted = current.format(formatter);

            Order order = modelMapper.map(createOrderRequest, Order.class);
            order.setIsPaid(createOrderRequest.getIsPaid());

            order.setCreatedAt(formatted);
            order.setUpdatedAt(formatted);

            Order createdOrder = orderRepository.save(order);
            if (createdOrder!=null){
                sendEmailService.sendEmailCreateOrder(createdOrder.getEmail(), createdOrder.getOrderItems());
            }
            return ResponseEntity.ok().body("Order created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating order: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getAllOrderDetails(String id) throws Exception {
        try {
            List<Order> checkUser=orderRepository.findAllByUserOrderByCreatedAtDescUpdatedAtDesc(id);
            if (checkUser.isEmpty()) {
                MessageResponse errorResponse = new MessageResponse();
                errorResponse.setMessage("Invalid id");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            List<ViewOrderDTO> viewOrderDTOs = checkUser.stream()
                    .map(order -> modelMapper.map(order, ViewOrderDTO.class))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(viewOrderDTOs);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorMessage( "An error occurred during get order for user")
            );
        }
    }

    @Override
    public ResponseEntity<?> getOrderDetails(String id) throws Exception {
        try {
            Optional<Order> checkProduct=orderRepository.findById(id);
            if (checkProduct.isEmpty()) {
                MessageResponse errorResponse = new MessageResponse();
                errorResponse.setMessage("Invalid id");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            Order order=checkProduct.get();
            ViewOrderDTO viewOrderDTO=modelMapper.map(order,ViewOrderDTO.class);

            return ResponseEntity.ok(viewOrderDTO);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorMessage( "An error occurred during get details Order")
            );
        }
    }

    @Override
    public ResponseEntity<?> cancelOrderDetails(DeleteOrderRequest deleteOrderRequest) throws Exception {
        try {
            Optional<Order> checkOrder=orderRepository.findById(deleteOrderRequest.getId());
            if (checkOrder.isEmpty()) {
                MessageResponse errorResponse = new MessageResponse();
                errorResponse.setMessage("Invalid id");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            Optional<Product> checkProduct = null;
            Integer amount = 0;
            for (OrderItem orderItem : deleteOrderRequest.getOrderItems()) {
                 amount=orderItem.getAmount();
                 checkProduct=productRepository.findById(orderItem.getProduct());
            }
            Optional<User> checkUser=userRepository.findById(deleteOrderRequest.getUser());

            if (checkUser.isPresent()&& checkProduct.isPresent()){
                Product existingProduct = checkProduct.get();
                Integer countInStockUpdate = existingProduct.getCountInStock() + amount;
                Integer soldUpdate = existingProduct.getSold() - amount;
                existingProduct.setSold(soldUpdate);
                existingProduct.setCountInStock(countInStockUpdate);
                productRepository.save(existingProduct);
                orderRepository.deleteById(deleteOrderRequest.getId());
            }

            return ResponseEntity.ok("delete order successfully");

        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorMessage( "An error occurred during get details Order")
            );
        }
    }

    @Override
    public ResponseEntity<?> getAllOrder() throws Exception {
        try {
            List<Order> checkOrder=orderRepository.findAll();
            if (checkOrder.isEmpty()) {
                MessageResponse errorResponse = new MessageResponse();
                errorResponse.setMessage("Do not have order");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            List<ViewOrderDTO> viewOrderDTOs = checkOrder.stream()
                    .map(order -> modelMapper.map(order, ViewOrderDTO.class))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(viewOrderDTOs);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorMessage( "An error occurred during get all Order")
            );
        }
    }

}
