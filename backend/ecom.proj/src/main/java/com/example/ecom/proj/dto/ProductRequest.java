package com.example.ecom.proj.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private int id;
    private String name;
    private BigDecimal price;
    private String brand;
    private int stockQuantity;


}
