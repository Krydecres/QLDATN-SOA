package com.qldatn.dangky.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;

public class JwtUtil {

    // Phải trùng khớp 100% với Secret Key ở auth-service
    private static final String SECRET_KEY = "QLDATN_SOA_MICROSERVICES_SECRET_KEY_FOR_JWT_TOKEN_2026";
    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static Claims parseToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Thiếu hoặc sai định dạng Token xác thực (Authorization header)");
        }
        String token = authHeader.substring(7);
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token không hợp lệ hoặc đã hết hạn!");
        }
    }
}