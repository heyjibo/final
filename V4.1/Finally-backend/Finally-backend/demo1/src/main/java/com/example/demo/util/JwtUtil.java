package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Base64;

public class JwtUtil {
    // 修改点1：使用Base64编码后的密钥（直接替换下方字符串）
    private static final String SECRET_KEY = "eW91ci1zZWNyZXQta2V5LWhlcmUtMTIzNDU2Nzg5MC10aGlzLWlzLWEtZGVtby1rZXk=";

    public static String parseToken(String token) {
        SecretKey key = getSecretKey(); // 修改点2：直接调用公共方法
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
        return claims.getSubject();
    }

    private static SecretKey getSecretKey() {
        // 修改点3：直接解码SECRET_KEY（已经是Base64字符串）
        return Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(SECRET_KEY)
        );
    }

    public static String generateToken(String workId) {
        return Jwts.builder()
                .setSubject(workId)
                .signWith(getSecretKey())
                .compact();
    }


}
