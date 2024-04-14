package com.pt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(value = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    @Value("${CLIENT_ID}")
    private String clientId;

    @GetMapping("config")
    public ResponseEntity<?> getTypeProduct( ) throws Exception {
        return ResponseEntity.ok(clientId);
    }
}
