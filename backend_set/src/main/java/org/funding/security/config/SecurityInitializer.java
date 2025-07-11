package org.funding.security.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

// spring security 필터체인을 dispatcherServlet에 등록
public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {
  // 별도 구현 불필요 - 상위 클래스에서 자동 처리
}