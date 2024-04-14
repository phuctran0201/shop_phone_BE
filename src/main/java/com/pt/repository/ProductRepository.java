package com.pt.repository;

import com.pt.entity.Order;
import com.pt.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface ProductRepository extends MongoRepository<Product,String> {

    Optional<Product> findByName(String name);

    Page<Product> findAll(Pageable pageable);

    Page<Product> findAllByNameIgnoreCaseContaining(String name,Pageable pageable);

    Page<Product> findAllByTypeIgnoreCaseContaining(String type,Pageable pageable);

    Page<Product> findAllByPrice(Double price,Pageable pageable);

    List<Product> findAll();

    default List<String> getAllTypes() {
        List<Product> allProducts = findAll();
        return allProducts.stream()
                .map(Product::getType)
                .distinct()
                .collect(Collectors.toList());
    }

}
