package org.funding.emailAuth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import org.funding.emailAuth.dao.EmailDAO;
import org.funding.emailAuth.vo.EmailAuthVO;
import org.funding.emailAuth.service.EmailService;
import org.funding.global.error.ErrorCode;
import org.funding.security.util.Auth;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "이메일 인증 API")
@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailService emailService;
    private final EmailDAO emailDAO;

    @ApiOperation(value = "이메일 인증 코드 발송", notes = "회원가입을 위해 입력된 이메일로 6자리 인증 코드를 발송합니다.")
    @PostMapping(value="/send", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, String>> sendCode(
            @ApiParam(value = "인증 코드를 받을 이메일 주소", required = true, example = "user@example.com") @RequestParam String email) {
        try {
            String code = emailService.sendVerificationEmail(email);
            emailDAO.insertAuthCode(email, code);
            Map<String, String> response = Map.of("message", "인증 코드가 이메일로 전송되었습니다.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = Map.of("message", "이메일 전송 실패: " + e.getMessage());
            return ResponseEntity.status(ErrorCode.ERROR_EMAIL.getStatus()).body(response);
        }
    }

    @ApiOperation(value = "이메일 인증 코드 확인", notes = "발송된 인증 코드가 유효한지 확인합니다.")
    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyCode(
            @ApiParam(value = "인증한 이메일 주소", required = true, example = "user@example.com") @RequestParam String email,
            @ApiParam(value = "사용자가 입력한 6자리 인증 코드", required = true, example = "123456") @RequestParam String code) {
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
