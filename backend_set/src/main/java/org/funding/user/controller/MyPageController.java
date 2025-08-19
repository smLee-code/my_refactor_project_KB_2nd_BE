package org.funding.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.funding.keyword.dto.KeywordResponseDTO;
import org.funding.project.dto.response.ProjectListDTO;
import org.funding.security.util.Auth;
import org.funding.user.dto.*;
import org.funding.user.service.MyPageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.funding.votes.dto.VotesResponseDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "마이페이지 API")
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
    @Auth
    @GetMapping("")
    public ResponseEntity<?> getMyPageInfo(HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        try {
            MyPageResponseDTO myPageInfo = myPageService.getMyPageInfo(userId);
            return ResponseEntity.ok(myPageInfo);
        } catch (IllegalStateException e) { // 인증 실패
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) { // 서버 오류
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류가 발생했습니다: " + e.getMessage() + "\"}");
        }
    }

    @ApiOperation(value = "관심 키워드 조회", notes = "사용자의 현재 관심 키워드 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "조회 성공"),
        @ApiResponse(code = 401, message = "인증 실패")
    })
    @Auth
    @GetMapping("/keywords")
    public ResponseEntity<?> getMyKeywords(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        try {
            List<KeywordResponseDTO> keywords = myPageService.getMyKeywords(userId);
            return ResponseEntity.ok(keywords);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류가 발생했습니다: " + e.getMessage() + "\"}");
        }
    }

    @ApiOperation(value = "관심 키워드 수정", notes = "사용자의 관심 키워드 목록을 전체 교체 방식으로 수정합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "수정 성공"),
        @ApiResponse(code = 401, message = "인증 실패"),
        @ApiResponse(code = 400, message = "잘못된 요청")
    })
    @Auth
    @PutMapping("/keywords")
    public ResponseEntity<?> updateMyKeywords(@RequestBody List<String> keywords,
                                              HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            myPageService.updateMyKeywords(keywords, userId);
            return ResponseEntity.ok("키워드가 성공적으로 수정되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류가 발생했습니다: " + e.getMessage() + "\"}");
        }
    }

    @ApiOperation(value = "개인정보 수정", notes = "사용자의 개인정보(이름, 닉네임, 전화번호)를 수정합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "수정 성공"),
        @ApiResponse(code = 401, message = "인증 실패"),
        @ApiResponse(code = 400, message = "잘못된 요청")
    })
    @Auth
    @PutMapping("/account")
    public ResponseEntity<?> updateAccountInfo(@RequestBody UpdateAccountRequestDTO request,
                                               HttpServletRequest servletRequest) {
        Long userId = (Long) servletRequest.getAttribute("userId");
        try {
            myPageService.updateAccountInfo(request, userId);
            return ResponseEntity.ok("개인정보가 성공적으로 수정되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류가 발생했습니다: " + e.getMessage() + "\"}");
        }
    }


    @ApiOperation(value = "내 투표 조회", notes = "사용자가 투표한 프로젝트 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "조회 성공"),
        @ApiResponse(code = 401, message = "인증 실패")
    })
    @Auth
    @GetMapping("/votes")
    public ResponseEntity<?> getMyVotes(HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        try {
            List<VotesResponseDTO> votes = myPageService.getMyVotes(userId);
            return ResponseEntity.ok(votes);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류가 발생했습니다: " + e.getMessage() + "\"}");
        }
    }

    @ApiOperation(value = "작성한 프로젝트 조회", notes = "사용자가 작성한 프로젝트 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "조회 성공"),
        @ApiResponse(code = 401, message = "인증 실패")
    })
    @Auth
    @GetMapping("/projects")
    public ResponseEntity<?> getMyProjects(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            List<ProjectListDTO> projects = myPageService.getMyProjects(userId);
            return ResponseEntity.ok(projects);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류가 발생했습니다: " + e.getMessage() + "\"}");
        }
    }
} 