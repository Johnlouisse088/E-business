package com.example.ecom.proj.service;

import com.example.ecom.proj.dao.ProductRepository;
import com.example.ecom.proj.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private ProductRepository repo;

    @Autowired
    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> getALlProducts() {
        return repo.findAll();
    }

    public Product getProductById(int prodId) {
        return repo.findById(prodId).orElse(new Product());
    }

    public Product addProduct(Product product, MultipartFile imageFile) throws IOException {
        product.setImageName(imageFile.getOriginalFilename());
        product.setImageType(imageFile.getContentType());
        product.setImageData(imageFile.getBytes());
        return repo.save(product);
    }

    public Product updateProduct(int prodId, Product product, MultipartFile imageFile) throws IOException {

        Optional<Product> myProduct = repo.findById(prodId); // Check if null or non-null

        if (myProduct.isPresent()) {
            Product productExist = myProduct.get();  // get the value
            // Update fields
            productExist.setImageName(imageFile.getOriginalFilename());
            productExist.setImageType(imageFile.getContentType());
            productExist.setImageData(imageFile.getBytes());
            productExist.setId(product.getId());
            productExist.setBrand(product.getBrand());
            productExist.setName(product.getName());
            productExist.setCategory(product.getCategory());
            productExist.setDescription(product.getDescription());
            productExist.setReleaseDate(product.getReleaseDate());
            productExist.setProductAvailable(product.isProductAvailable());
            productExist.setPrice(product.getPrice());
            productExist.setStockQuantity(product.getStockQuantity());
            return repo.save(productExist);
        } else {
            return null;  // return null
        }
    }

    public void deleteProduct(int prodId) {
        repo.deleteById(prodId);
    }

    public List<Product> searchProducts(String keyword) {
        return repo.findByNameAndDescription(keyword);
    }
}

