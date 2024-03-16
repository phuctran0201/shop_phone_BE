package com.pt.service;

import com.pt.DTO.ViewProductDTO;
import com.pt.DTO.ViewUserDTO;
import com.pt.req.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ProductService {
    public List<ViewProductDTO> listProductData() throws Exception;

    public ResponseEntity<?>  createProduct(CreateProductRequest productRequest) throws Exception;

    public ResponseEntity<?> updateProduct(UpdateProductRequest updateProductRequest) throws Exception;

    public ResponseEntity<?> deleteProduct(String id) throws Exception;

    public ResponseEntity<?> productDetail(String id) throws Exception;

}
