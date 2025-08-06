package org.funding.security.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProcessor jwtProcessor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod handlerMethod) {
            boolean hasAuth = handlerMethod.getMethod().isAnnotationPresent(Auth.class)
                    || handlerMethod.getBeanType().isAnnotationPresent(Auth.class);

            if (!hasAuth) return true;

            // jwt 인증 처리
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 존재하지 않습니다.");
                return false;
            }

            String token = authHeader.substring(7);
            if (!jwtProcessor.validateToken(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
                return false;
            }

            String username = jwtProcessor.getUsername(token);
            request.setAttribute("username", username);

            return true;
        }

        return true;
    }
}
