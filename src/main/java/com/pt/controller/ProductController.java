package com.pt.controller;

import com.pt.DTO.ViewProductDTO;
import com.pt.DTO.ViewUserDTO;
import com.pt.entity.Product;
import com.pt.req.CreateProductRequest;
import com.pt.req.CreateUserRequest;
import com.pt.req.UpdateProductRequest;
import com.pt.req.UpdateUserRequest;
import com.pt.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    private final ModelMapper modelMapper;

    @GetMapping()
    public List<ViewProductDTO> getProducts() throws Exception {
        List<ViewProductDTO> viewProductDTOS=this.productService.listProductData();
        return viewProductDTOS;
    }
    @PostMapping()
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest createProductRequest) throws Exception {
        return ResponseEntity.ok(this.productService.createProduct(createProductRequest));
    }
    @PutMapping()
    public ResponseEntity<?> updateProduct(@RequestBody UpdateProductRequest updateProductRequest) throws Exception {


        LocalDateTime current = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String formatted = current.format(formatter);

        UpdateProductRequest product=new UpdateProductRequest();
        product.setId(updateProductRequest.getId());
        product.setName(updateProductRequest.getName());
        product.setImage(updateProductRequest.getImage());
        product.setPrice(updateProductRequest.getPrice());
        product.setDescription(updateProductRequest.getDescription());
        product.setDiscount(updateProductRequest.getDiscount());
        product.setCountInStock(updateProductRequest.getCountInStock());
        product.setRating(updateProductRequest.getRating());
        product.setSold(updateProductRequest.getSold());
        product.setType(updateProductRequest.getType());
        product.setUpdatedAt(formatted);
        return ResponseEntity.ok(this.productService.updateProduct(product));
    }
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteProduct( @PathVariable String id) throws Exception {
        return ResponseEntity.ok( this.productService.deleteProduct(id));
    }
    @GetMapping(path = "/detail/{id}")
    public ResponseEntity<?> getDetailUser( @PathVariable String id) throws Exception {
        return ResponseEntity.ok( this.productService.productDetail(id));
    }
}
