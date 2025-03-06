package com.example.ecom.proj.controller;

import com.example.ecom.proj.dto.ProductRequest;
import com.example.ecom.proj.dto.StripeResponse;
import com.example.ecom.proj.service.StripeCheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/product/v1")
public class ProductCheckoutController {

    @Autowired
    private StripeCheckoutService stripeCheckoutService;

    public ProductCheckoutController(StripeCheckoutService stripeCheckoutService) {
        this.stripeCheckoutService = stripeCheckoutService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkoutProduct(@RequestBody Map<String, Object> productRequest) {
        StripeResponse stripeResponse = stripeCheckoutService.checkoutProducts(productRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(stripeResponse);
    }

}
