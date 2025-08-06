package org.funding.user.controller;

import lombok.RequiredArgsConstructor;
import org.funding.security.util.JwtProcessor;
import org.funding.user.dto.MemberLoginDTO;
import org.funding.user.dto.MemberSignupDTO;
import org.funding.user.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtProcessor jwtProcessor;

    @PostMapping(value = "/signup", produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> signup(@RequestBody MemberSignupDTO signupDTO) {
        memberService.signup(signupDTO);
        return ResponseEntity.ok("회원 가입 성공");
    }

    @PostMapping(value = "/login", produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> login(@RequestBody MemberLoginDTO loginDTO) {
        String jwt = memberService.login(loginDTO.getEmail(), loginDTO.getPassword());
        return ResponseEntity.ok(jwt);
    }

    // 테스트용 토큰 생성
    @PostMapping(value = "/test/token/generate")
    public ResponseEntity<String> generateToken() {
        // 테스트용 하드코딩 사용자 정보
        String testUsername = "testUser";
        String testRole = "ROLE_NORMAL";

        String jwt = jwtProcessor.generateTokenWithRole(testUsername, testRole);

        return ResponseEntity.ok(jwt);
    }
}
