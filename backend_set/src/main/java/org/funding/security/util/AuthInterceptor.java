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
            // 1. JWT 토큰 처리 로직을 항상 실행
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtProcessor.validateToken(token)) {
                    Long userId = jwtProcessor.getUserId(token);
                    if (userId != null) {
                        request.setAttribute("userId", userId);
                    }
                }
            }

            // 2. @Auth 어노테이션 확인
            boolean hasAuth = handlerMethod.getMethod().isAnnotationPresent(Auth.class)
                    || handlerMethod.getBeanType().isAnnotationPresent(Auth.class);

            // 3. @Auth가 필요한데, userId가 없는 경우에만 차단
            if (hasAuth && request.getAttribute("userId") == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요한 요청입니다.");
                return false;
            }
        }

        return true;

        // 혹시 몰라서 이전 로직 남겨둠!
//        if (handler instanceof HandlerMethod handlerMethod) {
//            boolean hasAuth = handlerMethod.getMethod().isAnnotationPresent(Auth.class)
//                    || handlerMethod.getBeanType().isAnnotationPresent(Auth.class);
//
//            if (!hasAuth) return true;
//
//            // jwt 인증 처리
//            String authHeader = request.getHeader("Authorization");
//            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 존재하지 않습니다.");
//                return false;
//            }
//
//            String token = authHeader.substring(7);
//            if (!jwtProcessor.validateToken(token)) {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
//                return false;
//            }
//
//            Long userId = jwtProcessor.getUserId(token);
//            if (userId == null) {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰에서 userId를 찾을 수 없습니다.");
//                return false;
//            }
//
//            request.setAttribute("userId", userId);
//
//            return true;
//        }
//
//        return true;
    }
}
