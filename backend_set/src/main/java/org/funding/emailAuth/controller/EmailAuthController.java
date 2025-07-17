package org.funding.emailAuth.controller;

import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import org.funding.emailAuth.dao.EmailDAO;
import org.funding.emailAuth.vo.EmailAuthVO;
import org.funding.emailAuth.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailService emailService;
    private final EmailDAO emailDAO;

    @PostMapping(value="/send", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, String>> sendCode(@RequestParam String email) {
        String code = emailService.sendVerificationEmail(email);
        emailDAO.insertAuthCode(email, code);
        Map<String, String> response = Map.of("message", "인증 코드가 이메일로 전송되었습니다.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyCode(@RequestParam String email, @RequestParam String code) {
        EmailAuthVO auth = emailDAO.findAuthCode(email, code);
        if (auth != null) {
            // 코드 만료 여부 검증
            emailDAO.expiredCode(email, code);
            Map<String, String> response = Map.of("message", "이메일 인증 성공");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = Map.of("message", "이메일 인증 실패");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
