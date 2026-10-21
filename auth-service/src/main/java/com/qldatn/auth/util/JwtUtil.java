package com.qldatn.auth.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    // Khoá bí mật mã hóa token (tối thiểu 32 ký tự cho HS256)
    private static final String SECRET_KEY = "QLDATN_SOA_MICROSERVICES_SECRET_KEY_FOR_JWT_TOKEN_2026";
    // Thời hạn token: 24 giờ
    private static final long EXPIRATION_TIME = 86400000;

    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static String generateToken(String username, String role, String maSV) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("maSV", maSV != null ? maSV : "");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}