package org.funding.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.funding.security.account.domain.CustomUser;
import org.funding.security.account.domain.MemberVO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Api(tags = "보안/인증 테스트 API")
@Slf4j
@RequestMapping("/api/security")
@RestController
public class SecurityController {

  @ApiOperation(value = "모든 사용자 접근 가능", notes = "로그인 여부와 상관없이 누구나 접근할 수 있는 테스트용 API입니다.")
  @GetMapping("/all")
  public ResponseEntity<String> doAll() {
    return ResponseEntity.ok("접근 가능");
  }

  @ApiOperation(value = "로그인한 사용자 접근 가능", notes = "로그인한 사용자(일반, 관리자 등)만 접근할 수 있는 테스트용 API입니다.")
  @GetMapping("/member")
  public ResponseEntity<String> doMember(Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    log.info("username = " + userDetails.getUsername());
    return ResponseEntity.ok(userDetails.getUsername());
  }

  @ApiOperation(value = "관리자만 접근 가능", notes = "관리자(ADMIN) 권한을 가진 사용자만 접근할 수 있는 테스트용 API입니다.")
  @GetMapping("/admin")
  public ResponseEntity<MemberVO> doAdmin(
          @AuthenticationPrincipal CustomUser customUser
  ) {
    MemberVO member = customUser.getMember();
    log.info("username = " + member);
    return ResponseEntity.ok(member);
  }
}