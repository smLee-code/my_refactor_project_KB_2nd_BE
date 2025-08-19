package org.funding.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtProcessor implements InitializingBean {

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.token-validity-in-milliseconds}")
  private long tokenValidMillisecond;
  private Key key;

  @Override
  public void afterPropertiesSet() throws Exception {
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(String subject) {
    return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + tokenValidMillisecond))
            .signWith(key) // ✅ 안전하게 초기화된 key 사용
            .compact();
  }

  public String generateTokenWithRole(String subject, String role) {
    return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + tokenValidMillisecond))
            .claim("role", role)
            .signWith(key)
            .compact();
  }

  public String generateTokenWithUserIdAndRole(String subject, Long userId, String role) {
    return Jwts.builder()
            .setSubject(subject)
            .claim("userId", userId)
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + tokenValidMillisecond))
            .signWith(key)
            .compact();
  }


  public String generateTokenWithUserId(String subject, Long userId) {
    return Jwts.builder()
            .setSubject(subject)
            .claim("userId", userId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + tokenValidMillisecond))
            .signWith(key)
            .compact();
  }

  public String generateTokenWithExpiry(String subject, Long tokenValidTime) {
    return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + tokenValidTime))
            .signWith(key)
            .compact();
  }

  public Long getUserId(String token) {
    Claims claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();

    Object userIdObj = claims.get("userId");
    if (userIdObj == null) return null;

    if (userIdObj instanceof Integer) {
      return ((Integer) userIdObj).longValue();
    } else if (userIdObj instanceof Long) {
      return (Long) userIdObj;
    } else if (userIdObj instanceof String) {
      return Long.parseLong((String) userIdObj);
    }
    return null;
  }

  public String getUsername(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
  }

  public String getRole(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("role", String.class);
  }

  public boolean validateToken(String token) {
    try {
      Jws<Claims> claims = Jwts.parserBuilder()
              .setSigningKey(key)
              .build()
              .parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      log.error("JWT 검증 실패: {}", e.getMessage());
      return false;
    }
  }
}