package org.funding.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.funding.security.util.JsonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(
          HttpServletRequest request,
          HttpServletResponse response,
          AuthenticationException authException
  ) throws IOException {

    log.error("========== 인증 에러 ============");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");
    PrintWriter writer = response.getWriter();
    writer.write("{\"error\":\"Unauthorized\", \"message\":\"" + authException.getMessage() + "\"}");
    writer.flush();
  }
}
