package org.funding.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.funding.security.util.JwtProcessor;
import org.funding.user.dto.MemberLoginDTO;
import org.funding.user.dto.MemberLoginResponseDTO;
import org.funding.user.dto.MemberSignupDTO;
import org.funding.user.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "회원/인증 API")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtProcessor jwtProcessor;

    @ApiOperation(value = "회원가입", notes = "새로운 회원을 등록합니다.")
    @PostMapping(value = "/signup", produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> signup(@RequestBody MemberSignupDTO signupDTO) {
        memberService.signup(signupDTO);
        return ResponseEntity.ok("회원 가입 성공");
    }

    @ApiOperation(value = "로그인", notes = "이메일과 비밀번호로 로그인하고, JWT 토큰을 발급받습니다.")
    @PostMapping(value = "/login", produces = "application/json; charset=UTF-8")
    public ResponseEntity<MemberLoginResponseDTO> login(@RequestBody MemberLoginDTO loginDTO) {
        MemberLoginResponseDTO response = memberService.login(loginDTO.getEmail(), loginDTO.getPassword());
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "이메일 중복 확인", notes = "회원가입 시 입력한 이메일이 이미 사용 중인지 확인합니다.")
    @GetMapping(value = "/duplicated/email")
    public ResponseEntity<Boolean> checkEmailDuplicate(
            @ApiParam(value = "중복 확인할 이메일", required = true, example = "user@example.com") @RequestParam String email) {
        Boolean isDuplicated = memberService.checkEmailDuplicate(email);
        return ResponseEntity.ok(isDuplicated);
    }

    @ApiOperation(value = "닉네임 중복 확인", notes = "회원가입 시 입력한 닉네임이 이미 사용 중인지 확인합니다.")
    @GetMapping(value = "/duplicated/nickname")
    public ResponseEntity<Boolean> checkNicknameDuplicate(
            @ApiParam(value = "중복 확인할 닉네임", required = true, example = "홍길동") @RequestParam String nickname) {
        Boolean isDuplicated = memberService.checkNicknameDuplicate(nickname);
        return ResponseEntity.ok(isDuplicated);
    }
}