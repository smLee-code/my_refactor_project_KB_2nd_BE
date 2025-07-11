package org.funding.security.filter;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.funding.security.util.JwtProcessor;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


  // Authorization 헤더명
  public static final String AUTHORIZATION_HEADER = "Authorization";

  // Bearer 토큰 접두사 (끝에 공백 포함)
  public static final String BEARER_PREFIX = "Bearer ";

  // JWT 처리를 위한 유틸리티
  private final JwtProcessor jwtProcessor;

  // 사용자 인증에 필요한 정보를 DB에서 조회해서 담는 커스텀 UserDetailService
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
          HttpServletRequest request,
          HttpServletResponse response,
          FilterChain filterChain
  ) throws ServletException, IOException {

    // Authorization 헤더에서 Bearer Token 추출
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

    if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
      // Bearer 접두사 제거하여 순수 토큰 추출
      String token = bearerToken.substring(BEARER_PREFIX.length());

      // 토큰에서 사용자 정보 추출 및 Authentication 객체 구성
      Authentication authentication = getAuthentication(token);

      // *** SecurityContext에 인증 정보 저장 ***
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 다음 필터로 요청 전달
    super.doFilter(request, response, filterChain);
  }


  private Authentication getAuthentication(String token) {

    // 토큰에서 사용자명 추출
    String username = jwtProcessor.getUsername(token);

    // 사용자 정보 로드
    UserDetails principal = userDetailsService.loadUserByUsername(username);

    // Authentication 객체 생성 및 반환
    return new UsernamePasswordAuthenticationToken(
            principal, null, principal.getAuthorities()
    );
  }
}