package org.funding.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.funding.security.filter.AuthenticationErrorFilter;
import org.funding.security.filter.JwtAuthenticationFilter;
import org.funding.security.filter.JwtUsernamePasswordAuthenticationFilter;
import org.funding.security.handler.CustomAccessDeniedHandler;
import org.funding.security.handler.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CharacterEncodingFilter;

// 필수 import 구문들
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity  // Spring Security 활성화
@Slf4j
@MapperScan(basePackages = {"org.funding.security.account.mapper"})  // 매퍼 스캔 설정

@ComponentScan(basePackages = {"org.funding.security"})    // 서비스 클래스 스캔
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  /* 필드  추가 */
  // 사용자 정보 조회
  private final UserDetailsService userDetailsService;   // CustomUserDetailsService 주입


  // 인증 예외 처리 필터
  // 인증 과정중 발생한 예외 먼저 캐치
  private final AuthenticationErrorFilter authenticationErrorFilter;

  // JWT 인증 필터
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  // 401/403 에러 처리 핸들러
  private final CustomAccessDeniedHandler accessDeniedHandler; // 403
  private final CustomAuthenticationEntryPoint authenticationEntryPoint; // 401


  // 커스텀 인증 필터 추가
  // 로그인시 아이디/비번 인증 후 JWT 발급
  @Autowired
  private JwtUsernamePasswordAuthenticationFilter jwtUsernamePasswordAuthenticationFilter;




  // PasswordEncoder(BCryptPasswordEncoder) Bean 등록 설정
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();  // BCrypt 해시 함수 사용
  }




  // 문자셋 필터 메서드
  public CharacterEncodingFilter encodingFilter() {
    CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
    encodingFilter.setEncoding("UTF-8");           // UTF-8 인코딩 설정
    encodingFilter.setForceEncoding(true);         // 강제 인코딩 적용
    return encodingFilter;
  }

  // AuthenticationManager 빈 등록 - JWT 토큰 인증에서 필요
  @Bean
  public AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }


  @Override
  public void configure(HttpSecurity http) throws Exception {
    // CSRF 필터보다 앞에 인코딩 필터 추가
    // - CSRF 필터는 Spring Security 환경에서 기본적으로 활성화 되어있음!
    http
        // 문자 인코딩
        .addFilterBefore(encodingFilter(), CsrfFilter.class)
        // 인증 에러 필터
        .addFilterBefore(authenticationErrorFilter, UsernamePasswordAuthenticationFilter.class)
        // JWT 인증필터
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        // API 로그인 인증 필터 추가 (기존 UsernamePasswordAuthenticationFilter 앞에 배치)
        .addFilterBefore(jwtUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

        // 예외 처리 설정
        .exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPoint)  // 401 에러 처리
        .accessDeniedHandler(accessDeniedHandler);           // 403 에러 처리



    //  HTTP 보안 설정
    http.httpBasic().disable()      // 기본 HTTP 인증 비활성화
            .csrf().disable()           // CSRF 보호 비활성화 (REST API에서는 불필요)
            .formLogin().disable()      // 폼 로그인 비활성화 (JSON 기반 API 사용)
            .sessionManagement()        // 세션 관리 설정
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);  // 무상태 모드


    http
      .authorizeRequests() // 경로별 접근 권한 설정
      .antMatchers(HttpMethod.OPTIONS).permitAll()  //  org.springframework.http.HttpMethod
      .antMatchers("/api/security/all").permitAll()                    // 모두 허용
      .antMatchers("/api/security/member").access("hasRole('ROLE_MEMBER')")  // ROLE_MEMBER 이상
      .antMatchers("/api/security/admin").access("hasRole('ROLE_ADMIN')")    // ROLE_ADMIN 이상
      .anyRequest().authenticated(); // 나머지는 로그인 필요

  }




  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    log.info("configure .........................................");

    // UserDetailsService와 PasswordEncoder 설정
    auth.userDetailsService(userDetailsService)  // 커스텀 서비스 사용
            .passwordEncoder(passwordEncoder()); // BCrypt 암호화 사용

  }



  // 브라우저의 CORS 정책을 우회하여 다른 도메인에서의 API 접근 허용
  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();

    config.setAllowCredentials(true);           // 인증 정보 포함 허용
    config.addAllowedOriginPattern("*");        // 모든 도메인 허용
    config.addAllowedHeader("*");               // 모든 헤더 허용
    config.addAllowedMethod("*");               // 모든 HTTP 메서드 허용

    source.registerCorsConfiguration("/**", config);  // 모든 경로에 적용
    return new CorsFilter(source);
  }

  // Spring Security 검사를 우회할 경로 설정
  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(
            "/assets/**",      // 정적 리소스
            "/*",              // 루트 경로의 파일들
            "/api/member/**"   // 회원 관련 공개 API
    );
  }
}