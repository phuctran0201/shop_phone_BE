package com.pt.repository;

import com.pt.entity.Order;
import com.pt.entity.Product;
import com.pt.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order,String> {
    List<Order> findAllByUserOrderByCreatedAtDescUpdatedAtDesc(String id);
}
