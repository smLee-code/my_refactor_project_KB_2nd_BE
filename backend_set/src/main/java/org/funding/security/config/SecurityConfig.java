package org.funding.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.funding.security.filter.AuthenticationErrorFilter;
import org.funding.security.filter.JwtAuthenticationFilter;
import org.funding.security.filter.JwtUsernamePasswordAuthenticationFilter;
import org.funding.security.handler.CustomAccessDeniedHandler;
import org.funding.security.handler.CustomAuthenticationEntryPoint;
import org.funding.security.handler.LoginFailureHandler;
import org.funding.security.handler.LoginSuccessHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
@Slf4j
@MapperScan(basePackages = {"org.funding.security.account.mapper"})
@ComponentScan(basePackages = {"org.funding.security", "org.funding.user.service"})
@RequiredArgsConstructor
//@EnableWebMvc
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
            .cors()
            .and()
            // 필터 등록 순서
            .addFilterBefore(encodingFilter(), org.springframework.security.web.csrf.CsrfFilter.class)
            .addFilterBefore(authenticationErrorFilter, UsernamePasswordAuthenticationFilter.class)
            //            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//            .addFilterBefore(jwtUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

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
                            "/ai/{fundId}/ai-recommend",
                            "/badge/all/badge",
                            "/retryVotes/do",
                            "/retryVotes/cancel",
                            "/ai/analyze-image",
                            "/api/project/**",
                            "/api/category/**",
                            "/api/keyword/**",
                            "/api/mypage/**",
//                            "/chat-app/**",
//                            "/topic/**",
                            "/api/chat/history/**",
                            "/api/app/chat/history/**", "/api/votes/**",
                            "/health", // 헬스체커 api 항상 열어놓을것!
                            "/s3/images",
                            "/userChallenge/{id}/verify",
                            "/userChallenge/{id}",
                            "/user-saving/saving",
                            "/user-saving/donation/{id}",
                            "/user-saving/donation-history/{id}",
                            "/user-saving/{id}",
                            "/user-saving/{id}",
                            "/user-loan/{id}",
                            "/user-loan/{id}",
                            "/user-loan/approve",
                            "/user-loan/reject",
                            "/user-loan/payment",
                            "/userSaving/apply",
                            "/userSaving/cancel/{id}",
                            "/userSaving/{id}",
                            "/userSaving/user/{id}",
                            "/badge/create",
                            "/badge/all/badge",
                            "/badge/{id}"
                    ).permitAll()
            .antMatchers("/api/security/all").permitAll()
            .antMatchers("/api/security/member").hasRole("MEMBER")
            .antMatchers("/api/security/admin").hasRole("ADMIN")
            .antMatchers("/api/fund/**").permitAll()  // 펀딩 API 테스트용 - 추후 인증 필요시 제거
           //.antMatchers("/api/fund/create/**").hasRole("FINANCE")
           //.antMatchers("/api/fund/list").permitAll()
           //..antMatchers("/api/fund/admin").hasRole("ADMIN")
            .antMatchers("/api/project/list/detail/**").permitAll()  // detail 조회는 누구나
            .antMatchers("/api/payments/**").permitAll()  // 임시로 결제 API 인증 없이 허용
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
    config.addAllowedOrigin("http://localhost:5173");

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
            "/api/project/**",
            "/api/comment/**",
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
            "/chat-app/**",
            "/websocket/**",
            "/ws/**",      // 혹시 사용하는 경로가 다를 경우 대비
            "/topic/**",
            "/api/chat/history/**",

            "/badge/all/badge",
            "/ai/{fundId}/ai-recommend",

            "/api/votes/**",
            "/health", // 헬스체커 api 항상 열어놓을것!
            "/s3/images",
            "/userChallenge/{id}/verify",
            "/userChallenge/{id}",
            "/user-saving/saving",
            "/user-saving/donation/{id}",
            "/user-saving/donation-history/{id}",
            "/user-saving/{id}",
            "/user-saving/{id}",
            "/user-loan/{id}",
            "/user-loan/{id}",
            "/user-loan/approve",
            "/user-loan/reject",
            "/user-loan/payment",
            "/userSaving/apply",
            "/userSaving/cancel/{id}",
            "/userSaving/{id}",
            "/userSaving/user/{id}",
            "/badge/create",
            "/badge/all/badge",
            "/badge/{id}"
    );
  }
}