package org.funding.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtProcessor {

  // 테스트용 5분 - 만료 확인용
  static private final long TOKEN_VALID_MILLISECOND = 1000L * 60 * 5;

  // 개발용 고정 Secret Key
  private String secretKey = "8oP5JazHXh8E7NAS48xCgHIgdwL/7BvetKx+CfGvIqk=";
  private Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));



  public String generateToken(String subject) {
    return Jwts.builder()
            .setSubject(subject)                    // 사용자 식별자
            .setIssuedAt(new Date())               // 발급 시간
            .setExpiration(new Date(new Date().getTime() + TOKEN_VALID_MILLISECOND))  // 만료 시간
            .signWith(key)                         // 서명
            .compact();                            // 문자열 생성
  }

  public String generateTokenWithRole(String subject, String role) {
    return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(new Date())
            .setExpiration(new Date(new Date().getTime() + TOKEN_VALID_MILLISECOND))
            .claim("role", role)                   // 권한 정보 추가
            .signWith(key)
            .compact();
  }

  public String generateTokenWithUserIdAndRole(String subject, Long userId, String role) {
    return Jwts.builder()
            .setSubject(subject)
            .claim("userId", userId)
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALID_MILLISECOND))
            .signWith(key)
            .compact();
  }


  public String generateTokenWithUserId(String subject, Long userId) {
    return Jwts.builder()
            .setSubject(subject)                    // 사용자 식별자 (username 등)
            .claim("userId", userId)                // userId 클레임 추가
            .setIssuedAt(new Date())
            .setExpiration(new Date(new Date().getTime() + TOKEN_VALID_MILLISECOND))
            .signWith(key)
            .compact();
  }


  public String generateTokenWithExpiry(String subject, Long tokenValidTime) {
    return Jwts.builder()
            .setSubject(subject)                    // 사용자 식별자
            .setIssuedAt(new Date())               // 발급 시간
            .setExpiration(new Date(new Date().getTime() + tokenValidTime))  // 만료 시간
            .signWith(key)                         // 서명
            .compact();                            // 문자열 생성
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
    return null;  // 타입이 예상과 다르면 null 반환
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
