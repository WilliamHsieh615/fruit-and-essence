package com.williamhsieh.fruitandessence.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JwtUtil {

    private static final String SECRET = "this is my very secret password, i wish you never understand";
    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    // 產生 Access Token
    private static final long ACCESS_EXPIRATION = 1000 * 60 * 30; // 30分鐘
    private static final long REFRESH_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7天

    // 產生 Access Token
    public static String generateAccessToken(Integer memberId, String email, String rolesCsv) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ACCESS_EXPIRATION);

        return Jwts.builder()
                .setSubject(email) // subject 為 email（可改）
                .setIssuedAt(now)
                .setExpiration(exp)
                // 使用 memberId 作為 claim（你專案裡使用 memberId）
                .claim("memberId", memberId)
                .claim("roles", rolesCsv) // e.g. "ADMIN,USER"
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateAccessToken(Integer memberId, String email, List<String> roles) {
        String rolesCsv = roles.stream().collect(Collectors.joining(","));
        return generateAccessToken(memberId, email, rolesCsv);
    }

    // 產生 Refresh Token
    public static String generateRefreshToken(Integer memberId) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + REFRESH_EXPIRATION);

        return Jwts.builder()
                .setSubject(memberId.toString())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 Token
    public static Claims parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 驗證是否過期
    public static boolean isExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

}
