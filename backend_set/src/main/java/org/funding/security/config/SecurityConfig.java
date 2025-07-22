package org.funding.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.funding.security.handler.LoginFailureHandler;
import org.funding.security.handler.LoginSuccessHandler;
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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebSecurity
@Slf4j
@MapperScan(basePackages = {"org.funding.security.account.mapper"})
@ComponentScan(basePackages = {"org.funding.security"})
@RequiredArgsConstructor
@EnableWebMvc
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserDetailsService userDetailsService;
  private final AuthenticationErrorFilter authenticationErrorFilter;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CustomAccessDeniedHandler accessDeniedHandler;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;
  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;


  // JwtUsernamePasswordAuthenticationFilter는 아래에서 빈으로 등록한다.
  @Autowired
  private JwtUsernamePasswordAuthenticationFilter jwtUsernamePasswordAuthenticationFilter;

  // PasswordEncoder 빈 등록
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // 문자 인코딩 필터 생성
  public CharacterEncodingFilter encodingFilter() {
    CharacterEncodingFilter filter = new CharacterEncodingFilter();
    filter.setEncoding("UTF-8");
    filter.setForceEncoding(true);
    return filter;
  }

  // AuthenticationManager 빈 등록 (JwtUsernamePasswordAuthenticationFilter 생성자에 주입용)
  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  // JwtUsernamePasswordAuthenticationFilter 빈으로 등록, 생성자에 AuthenticationManager 주입
  @Bean
  public JwtUsernamePasswordAuthenticationFilter jwtUsernamePasswordAuthenticationFilter() throws Exception {
    return new JwtUsernamePasswordAuthenticationFilter(
            authenticationManagerBean(),
            loginSuccessHandler,
            loginFailureHandler
    );
  }


  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    log.info("configure AuthenticationManagerBuilder");
    auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
            // 필터 등록 순서
            .addFilterBefore(encodingFilter(), org.springframework.security.web.csrf.CsrfFilter.class)
            .addFilterBefore(authenticationErrorFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            // 예외 처리 핸들러
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
            .and()

            // 기본 보안 설정
            .httpBasic().disable()
            .csrf().disable()
            .formLogin().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);


            // 권한 설정
            http
                    .authorizeRequests()
                    .antMatchers("/swagger-ui.html",
                            "/swagger-ui/**",
                            "/v2/api-docs",
                            "/v3/api-docs",
                            "/swagger-resources/**",
                            "/webjars/**",
                            "/mail/send",
                            "mail/verify",
                            "/ai/ask",
                            "/ai/fund",
                            "/badge/create",
                            "/badge/{id}",
                            "/badge/all/badge").permitAll()
            .antMatchers("/api/security/all").permitAll()
            .antMatchers("/api/security/member").hasRole("MEMBER")
            .antMatchers("/api/security/admin").hasRole("ADMIN")
            .antMatchers("/api/fund/**").permitAll()  // 펀딩 API 테스트용 - 추후 인증 필요시 제거 ROLE_FINANCE
           //.antMatchers("/api/fund/**").hasRole("FINANCE")
            .anyRequest().authenticated();
  }

  // CORS 필터 빈 등록 (필요하면)
  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();

    config.setAllowCredentials(true);
    config.addAllowedOriginPattern("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");

    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }

  // WebSecurity에서 검사 제외할 경로
  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(
            "/assets/**",
            "/*",
            "/api/member/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/mail/send",
            "/mail/verify",
            "/ai/ask",
            "/ai/fund",
            "/badge/create",
            "/badge/{id}",
            "/badge/all/badge"
    );
  }
}