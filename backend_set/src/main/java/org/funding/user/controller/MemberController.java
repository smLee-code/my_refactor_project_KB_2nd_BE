package org.funding.user.controller;

import lombok.RequiredArgsConstructor;
import org.funding.user.service.MemberService;
import org.funding.user.vo.MemberLoginVO;
import org.funding.user.vo.MemberVO;
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

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody MemberVO memberVO) {
        memberService.signup(memberVO);
        return ResponseEntity.ok("회원 가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody MemberLoginVO memberLoginVO) {
        String jwt = memberService.login(memberLoginVO.getUsername(), memberLoginVO.getPassword());
        return ResponseEntity.ok(jwt);
    }
}
