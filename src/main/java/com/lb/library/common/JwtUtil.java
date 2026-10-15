package com.lb.library.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类：生成 token、解析 token
 * 注意：生产环境密钥应放到配置中心或环境变量，不要硬编码
 */
public class JwtUtil {

    /** 签名密钥（HS256 算法要求至少 32 字节） */
    private static final String SECRET = "lb-library-jwt-secret-2026-please-keep-it-safe!!";

    /** token 有效期：24 小时 */
    private static final long EXPIRE_MS = 24 * 60 * 60 * 1000L;

    private static SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /** 生成 token，携带 userId 和 role */
    public static String createToken(Integer userId, Integer role) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRE_MS))
                .signWith(getKey())
                .compact();
    }

    /** 解析 token，非法或过期会抛 JwtException */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
