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
    // Store user-specific secret keys (in a real app, this should be in a database)
    private final Map<String, String> userSecrets = new ConcurrentHashMap<>();
    private final long expiration = 1000 * 60 * 60; // 1 hour

    // Generate a secure random key for a user
    public String generateSecretKeyForUser(String username) {
        byte[] keyBytes = new byte[64]; // 512 bits for HS512
        new SecureRandom().nextBytes(keyBytes);
        String secretKey = Base64.getEncoder().encodeToString(keyBytes);

        // Store the secret key for this user
        userSecrets.put(username, secretKey);
        return secretKey;
    }

    // Get the secret key for a user (or generate one if it doesn't exist yet)
    private String getSecretKeyForUser(String username) {
        return userSecrets.computeIfAbsent(username, this::generateSecretKeyForUser);
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