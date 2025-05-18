package com.lab1.datingapp.jwt;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtil {
    private final Map<String, String> userSecrets = new ConcurrentHashMap<>();
    private final long expiration = 1000 * 60 * 60; // 1 hour

    public String generateSecretKey() {
        byte[] keyBytes = new byte[64]; // 512 bits for HS512
        new SecureRandom().nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    // Get or generate secret key for a user
    private String getSecretKeyForUser(String username) {
        return userSecrets.computeIfAbsent(username, k -> generateSecretKey());
    }

    // Generate a JWT token for a user
    public String generateToken(String username) {
        String secretKey = getSecretKeyForUser(username);
        Key signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // Extract username from token
    public String extractUsername(String token) {
        token = cleanToken(token);
        // This is tricky because you need to know which user's secret to use
        // You'll need to extract the username without verification first
        String username = extractUsernameWithoutVerification(token);
        if (username != null && userSecrets.containsKey(username)) {
            String secretKey = userSecrets.get(username);
            Key signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

            try {
                return Jwts.parserBuilder()
                        .setSigningKey(signingKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    // Extract username without signature verification (for looking up the correct secret)
    private String extractUsernameWithoutVerification(String token) {
        try {
            // Split the token into parts
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                // Decode payload (2nd part) without verification
                String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
                // Simple JSON parsing to extract subject
                if (payload.contains("\"sub\"")) {
                    int start = payload.indexOf("\"sub\"") + 7;
                    int end = payload.indexOf("\"", start);
                    return payload.substring(start, end);
                }
            }
        } catch (Exception e) {
            // Ignore exceptions
        }
        return null;
    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            token = cleanToken(token);
            String username = extractUsernameWithoutVerification(token);
            if (username != null && userSecrets.containsKey(username)) {
                String secretKey = userSecrets.get(username);
                Key signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

                Jwts.parserBuilder()
                        .setSigningKey(signingKey)
                        .build()
                        .parseClaimsJws(token);
                return true;
            }
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Remove Bearer prefix
    private String cleanToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}