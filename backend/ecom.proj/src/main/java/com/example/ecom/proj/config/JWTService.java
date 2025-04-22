package com.example.ecom.proj.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${application.jwt.secret-key}")
    private String secretKey;

    @Value("${application.jwt.access-token.expiration}")
    private long accessExpiration;

    @Value("${application.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    // Call another method
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Generate token (claims, sub, issueAt, exp, ...)
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        // extraClaims - empty hashMap
        // calling another method to generate token
        return buildToken(extraClaims, userDetails, accessExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        System.out.println("secretKey: " + secretKey);
        // Generate token
        return Jwts
                .builder()
                .setClaims(extraClaims) // extraClaims - Passing empty hashMap; don’t have any extra data, you just pass an empty HashMap< > ()  // If you want one with custom info -> pass in Map.of("role", "ADMIN")
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {   // sub from the field in token, are the same to username (email)
        return extractClaim(token, Claims::getSubject);  // the token had sub, iat, exp; The sub is username (the email was called as a 'username';
        // example: sub:"test@gmail.com"
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails, String userEmail) {
        return (userEmail.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
