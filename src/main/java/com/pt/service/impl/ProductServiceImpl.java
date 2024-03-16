package com.pt.service.impl;

import com.pt.DTO.ViewProductDTO;
import com.pt.DTO.ViewUserDTO;
import com.pt.entity.Product;
import com.pt.entity.User;
import com.pt.exceptionMessage.MessageResponse;
import com.pt.repository.ProductRepository;
import com.pt.req.CreateProductRequest;
import com.pt.req.UpdateProductRequest;
import com.pt.service.ProductService;
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
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public List<ViewProductDTO> listProductData() throws Exception {
        List<Product> products = productRepository.findAll();
        List<ViewProductDTO> viewProductDTOS = products.stream()
                .map(product -> modelMapper.map(product, ViewProductDTO.class))
                .collect(Collectors.toList());
        return viewProductDTOS;
    }

    @Override
    public ResponseEntity<?>  createProduct(CreateProductRequest productRequest) throws Exception {
      try {
          String name = productRequest.getName();
          String image = productRequest.getImage();
          String type = productRequest.getType();
          Double price = productRequest.getPrice();
          Integer countInStock = productRequest.getCountInStock();
          Double rating = productRequest.getRating();
          String description=productRequest.getDescription();
          Double discount=productRequest.getDiscount();
          Integer sold=productRequest.getSold();
          if (name == null || image == null || type == null || price == null || countInStock==null) {
              MessageResponse errorResponse = new MessageResponse();
              errorResponse.setMessage("The input is required");
              errorResponse.setStatus("ERR");
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
          }
          Optional<Product> nameExisted=productRepository.findByName(name);
          if (nameExisted.isPresent()){
              MessageResponse errorResponse = new MessageResponse();
              errorResponse.setMessage("product already exists");
              errorResponse.setStatus("ERR");
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
          }
          Product product = modelMapper.map(productRequest, Product.class);
          LocalDateTime current = LocalDateTime.now();

          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

          String formatted = current.format(formatter);
          product.setCreatedAt(formatted);
          product.setUpdatedAt(formatted);

          productRepository.save(product);
          return ResponseEntity.ok("Create product successfully");
      }catch (Exception e){
          return ResponseEntity.status(500).body(
                  new ErrorMessage( "An error occurred during create product")
          );
      }
    }

    @Override
    public ResponseEntity<?> updateProduct(UpdateProductRequest updateProductRequest) throws Exception {
        try {
            Optional<Product> checkProduct=productRepository.findById(updateProductRequest.getId());
            if (checkProduct.isEmpty()) {
                MessageResponse errorResponse = new MessageResponse();
                errorResponse.setMessage("Invalid id");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            Product existingProduct = checkProduct.get();
            modelMapper.getConfiguration().setSkipNullEnabled(true);
            modelMapper.map(updateProductRequest, existingProduct);
            productRepository.save(existingProduct);
            MessageResponse successResponse = new MessageResponse();
            successResponse.setMessage("Product updated successfully");
            successResponse.setStatus("OK");
            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorMessage( "An error occurred during update Product")
            );
        }
}

    @Override
    public ResponseEntity<?> deleteProduct(String id) throws Exception {
        try {
            Optional<Product> checkProduct=productRepository.findById(id);
            if (checkProduct.isEmpty()) {
                MessageResponse errorResponse = new MessageResponse();
                errorResponse.setMessage("Invalid id");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            productRepository.deleteById(id);
            MessageResponse successResponse = new MessageResponse();
            successResponse.setMessage("Delete product successfully");
            successResponse.setStatus("OK");
            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorMessage( "An error occurred during delete Product")
            );
        }
    }

    @Override
    public ResponseEntity<?> productDetail(String id) throws Exception {
        try {
            Optional<Product> checkProduct=productRepository.findById(id);
            if (checkProduct.isEmpty()) {
                MessageResponse errorResponse = new MessageResponse();
                errorResponse.setMessage("Invalid id");
                errorResponse.setStatus("ERR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            Product product=checkProduct.get();
            ViewProductDTO viewProductDTO=modelMapper.map(product,ViewProductDTO.class);

            return ResponseEntity.ok(viewProductDTO);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorMessage( "An error occurred during delete Product")
            );
        }
    }
}
