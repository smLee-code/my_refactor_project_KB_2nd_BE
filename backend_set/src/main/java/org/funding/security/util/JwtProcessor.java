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
  private String secretKey = "4중대_1소대장_김태영_28사단_병장_김태영";
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

  public String generateTokenWithExpiry(String subject, Long tokenValidTime) {
    return Jwts.builder()
            .setSubject(subject)                    // 사용자 식별자
            .setIssuedAt(new Date())               // 발급 시간
            .setExpiration(new Date(new Date().getTime() + tokenValidTime))  // 만료 시간
            .signWith(key)                         // 서명
            .compact();                            // 문자열 생성
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
