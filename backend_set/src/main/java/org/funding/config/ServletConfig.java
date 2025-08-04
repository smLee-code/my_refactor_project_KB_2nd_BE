package org.funding.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import java.util.List;

/**
 * 🌐 Spring MVC Web Context 설정 클래스
 * - Spring MVC의 웹 계층(Presentation Layer)을 담당하는 컨텍스트 설정 클래스
 * - 사용자 요청 처리와 관련된 모든 웹 컴포넌트들을 관리하고 설정함
 * 
 * 📋 주요 어노테이션 설명:
 * @EnableWebMvc
 * - Spring MVC 기능을 활성화하는 핵심 어노테이션
 * - DispatcherServlet, HandlerMapping, HandlerAdapter 등 MVC 인프라 자동 설정
 * - JSON/XML 변환, 데이터 바인딩, 유효성 검증 등 웹 기능 활성화
 * - <mvc:annotation-driven />의 자바 설정 버전
 * 
 * @ComponentScan
 * - 지정된 패키지에서 Spring 컴포넌트를 자동으로 스캔하여 빈으로 등록
 * - 현재 설정: "org.scoula.controller" 패키지의 @Controller 클래스들을 스캔
 * 
 * WebMvcConfigurer 인터페이스 구현
 * - Spring MVC의 세부 설정을 커스터마이징할 수 있는 인터페이스
 * - 필요한 메서드만 오버라이드하여 선택적 설정 가능
 */
@EnableWebMvc
@ComponentScan(basePackages = {
        "org.funding.exception",
        "org.funding.controller",
        "org.funding.user.controller",
        "org.funding.user.service",
        "org.funding.config",
        "org.funding.emailAuth.controller",
        "org.funding.emailAuth.service",
        "org.funding.openAi.controller",
        "org.funding.openAi.service",
        "org.funding.openAi.client",
        "org.funding.badge.controller",
        "org.funding.badge.service",
        "org.funding.fund.controller",
        "org.funding.fund.service",
        "org.funding.retryVotes.service",
        "org.funding.retryVotes.controller",
        "org.funding.comment.controller",
        "org.funding.comment.service",
        "org.funding.config",
        "org.funding.project.controller",
        "org.funding.project.service",
        "org.funding.votes.controller",
        "org.funding.votes.service",
        "org.funding.chatting.controller",
        "org.funding.chatting.service",
        "org.funding.chatting.config",
        "org.funding.category.controller",
        "org.funding.category.service",
        "org.funding.keyword.controller",
        "org.funding.keyword.service",
        "org.funding.userKeyword.service",
        "org.funding.projectKeyword.service",
        "org.funding.chatting.config",
        "org.funding.payment.controller",
        "org.funding.payment.service",
        "org.funding.healthChecker.controller",
        "org.funding.S3.controller",
        "org.funding.S3.service",

}) // Spring MVC용 컴포넌트 등록을 위한 스캔 패키지
public class ServletConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/resources/**") // URL이 /resources/로 시작하는 모든 경로
                .addResourceLocations("/resources/"); // webapp/resources/ 경로로 매핑

        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/swagger-resources/**")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/v2/api-docs")
                .addResourceLocations("classpath:/META-INF/resources/");

    }


    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setViewClass(JstlView.class);           // JSTL 지원 활성화
        bean.setPrefix("/WEB-INF/views/");           // JSP 파일 기본 경로
        bean.setSuffix(".jsp");                      // JSP 파일 확장자
        registry.viewResolver(bean);                 // ViewResolver 등록
    }

    // 📍 Servlet 3.0 파일 업로드 설정
    @Bean
    public MultipartResolver multipartResolver() {
        StandardServletMultipartResolver resolver =
                new StandardServletMultipartResolver();
        return resolver;
    }

    // 📍 Jackson JSR310 (Java 8 Time API) 설정
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        converter.setObjectMapper(objectMapper);
        converters.add(converter);
    }

}
