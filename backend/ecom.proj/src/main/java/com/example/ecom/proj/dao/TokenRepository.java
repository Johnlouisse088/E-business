package com.example.ecom.proj.dao;

import com.example.ecom.proj.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Integer> {
}
