package org.funding.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.funding.user.dto.*;
import org.funding.user.service.MyPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @ApiOperation(value = "마이페이지 조회", notes = "사용자의 기본 정보 및 요약 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "조회 성공"),
        @ApiResponse(code = 401, message = "인증 실패"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("")
    public ResponseEntity<MyPageResponseDTO> getMyPageInfo() {
        MyPageResponseDTO myPageInfo = myPageService.getMyPageInfo();
        return ResponseEntity.ok(myPageInfo);
    }

    @ApiOperation(value = "관심 키워드 조회", notes = "사용자의 현재 관심 키워드 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "조회 성공"),
        @ApiResponse(code = 401, message = "인증 실패")
    })
    @GetMapping("/keywords")
    public ResponseEntity<List<KeywordResponseDTO>> getMyKeywords() {
        List<KeywordResponseDTO> keywords = myPageService.getMyKeywords();
        return ResponseEntity.ok(keywords);
    }

    @ApiOperation(value = "관심 키워드 수정", notes = "사용자의 관심 키워드 목록을 전체 교체 방식으로 수정합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "수정 성공"),
        @ApiResponse(code = 401, message = "인증 실패"),
        @ApiResponse(code = 400, message = "잘못된 요청")
    })
    @PutMapping("/keywords")
    public ResponseEntity<String> updateMyKeywords(@RequestBody UpdateKeywordsRequestDTO request) {
        myPageService.updateMyKeywords(request.getKeywords());
        return ResponseEntity.ok("키워드가 성공적으로 수정되었습니다.");
    }

    @ApiOperation(value = "개인정보 수정", notes = "사용자의 개인정보(이름, 닉네임, 전화번호)를 수정합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "수정 성공"),
        @ApiResponse(code = 401, message = "인증 실패"),
        @ApiResponse(code = 400, message = "잘못된 요청")
    })
    @PutMapping("/account")
    public ResponseEntity<String> updateAccountInfo(@RequestBody UpdateAccountRequestDTO request) {
        myPageService.updateAccountInfo(request);
        return ResponseEntity.ok("개인정보가 성공적으로 수정되었습니다.");
    }

    @ApiOperation(value = "내 투표 조회", notes = "사용자가 투표한 프로젝트 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "조회 성공"),
        @ApiResponse(code = 401, message = "인증 실패")
    })
    @GetMapping("/votes")
    public ResponseEntity<List<MyVoteResponseDTO>> getMyVotes() {
        List<MyVoteResponseDTO> votes = myPageService.getMyVotes();
        return ResponseEntity.ok(votes);
    }

    @ApiOperation(value = "작성한 프로젝트 조회", notes = "사용자가 작성한 프로젝트 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "조회 성공"),
        @ApiResponse(code = 401, message = "인증 실패")
    })
    @GetMapping("/projects")
    public ResponseEntity<List<MyProjectResponseDTO>> getMyProjects() {
        List<MyProjectResponseDTO> projects = myPageService.getMyProjects();
        return ResponseEntity.ok(projects);
    }


} 