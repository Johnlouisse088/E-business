package com.example.ecom.proj.dao;

import com.example.ecom.proj.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    // """...""" - multiline string in Java without needing \n
    // Get all tokens with revoked and expired are false
    @Query(value = """
            SELECT t.* FROM token t
            LEFT JOIN users u ON t.user_id = u.id
            WHERE t.user_id = :id
              AND t.expired = false 
              AND t.revoked = false
            """, nativeQuery = true)
    List<Token> findValidUserTokens(Integer id);
}
