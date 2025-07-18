package org.funding.project.controller;

import lombok.RequiredArgsConstructor;
import org.funding.project.service.ProjectService;
import org.funding.project.vo.ProjectVO;
import org.funding.user.service.MemberService;
import org.funding.user.vo.MemberLoginVO;
import org.funding.user.vo.MemberVO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;


    @GetMapping("/list/detail/{id}")
    public ResponseEntity<ProjectVO> getProjectDetail(@PathVariable("id") Long id) {
        ProjectVO project = projectService.selectProjectById(id);
        return ResponseEntity.ok(project);
    }
//    private final MemberService memberService;
//
//    @PostMapping("/signup")
//    public ResponseEntity<String> signup(@RequestBody MemberVO memberVO) {
//        memberService.signup(memberVO);
//        return ResponseEntity.ok("회원 가입 성공");
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody MemberLoginVO memberLoginVO) {
//        String jwt = memberService.login(memberLoginVO.getUsername(), memberLoginVO.getPassword());
//        return ResponseEntity.ok(jwt);
//    }
}