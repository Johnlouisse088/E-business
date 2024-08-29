package com.example.ecom.proj.controller;

import com.example.ecom.proj.model.Product;
import com.example.ecom.proj.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ProductController {

    private ProductService service;

    @Autowired
    public ProductController(ProductService service) {
        this.service = service;
    }

    // Get all products
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts() {
        return new ResponseEntity<>(service.getALlProducts(), HttpStatus.OK);
    }

    // Get product by Id
    @GetMapping("/products/{prodId}")
    public ResponseEntity<Product> getProductById(@PathVariable int prodId) {

        // return data whenever it's null or non-null
        Optional<Product> myProductOptional = Optional.ofNullable(service.getProductById(prodId));

        // check if data is present
        if (myProductOptional.isPresent()) {
            return new ResponseEntity<>(service.getProductById(prodId), HttpStatus.OK); // return OK status code
        } else {
            return new ResponseEntity<>(service.getProductById(prodId), HttpStatus.NOT_FOUND); // return NOT FOUND status code
        }
    }

    // Add product
    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@RequestPart Product product,
                                        @RequestPart MultipartFile imageFile) {
        try {
            return new ResponseEntity<>(service.addProduct(product, imageFile), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Image
    @GetMapping("/products/{prodId}/image")
    public byte[] getImage(@PathVariable int prodId) {
        Product product = service.getProductById(prodId);
        byte[] imageFile = product.getImageData();

        return ResponseEntity.ok().body(imageFile).getBody();
        // it only returns the body and status code (body 'imageFile'; status code 'ok'
        // compared to the new ResponseEntity<>(...) you can directly control
        // the construction of response (body, status code, headers)
    }

    // Update
    @PutMapping("/products/{prodId}")
    public ResponseEntity<?> updateProduct(@PathVariable int prodId,
                                                 @RequestPart MultipartFile imageFile,
                                                 @RequestPart Product product) throws IOException {

        Product productService = service.updateProduct(prodId, product, imageFile);

        if (productService != null) {
            return new ResponseEntity<>("Product updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Product update failed", HttpStatus.BAD_REQUEST);
        }
    }

    // Delete
    @DeleteMapping("/products/{prodId}")
    public ResponseEntity<?> deleteProduct (@PathVariable int prodId) {
        Product myProduct = service.getProductById(prodId);
        if (myProduct != null) {
            service.deleteProduct(prodId);
            return new ResponseEntity<>("The product is deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("The product is not exist", HttpStatus.BAD_REQUEST);
        }
    }

    // for searched product
    @GetMapping("/products/search")
    public ResponseEntity<?> getAllProducts(@RequestParam String keyword) {
        List<Product> products = service.searchProducts(keyword);
        return new ResponseEntity<>(products, HttpStatus.OK);

    }
}



