package com.example.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.backend.model.User;

import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    // 1. Read the secret key from application.properties
    @Value("${jwt.secret}")
    private String secret;
    
    // 2. This will be the secure key object used for signing
    private SecretKey key;

    // 3. This method runs once after the class is created
    @PostConstruct
    public void init() {
        // Convert the String secret into a secure Key object
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 4. --- PUBLIC METHODS ---

    /**
     * Generates a new JWT for a given user.
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        // You can add more claims here (e.g., roles)
        claims.put("name", user.getName());
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail()) // The "subject" is the user's email
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates a token. Returns true if valid, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            // Try to parse the token. If it works, it's valid.
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            // Token is invalid (expired, malformed, wrong signature)
            return false;
        }
    }
    
    /**
     * Gets the user's email from the token.
     */
    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }


    // 5. --- PRIVATE HELPER METHODS ---

    /**
     * Checks if a token is expired.
     */
    private boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    /**
     * Gets the expiration date from a token.
     */
    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Generic helper function to get any "claim" (field) from the token.
     */
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * The main parser function that reads the token.
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}