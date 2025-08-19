package org.funding.keyword.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.funding.keyword.dto.KeywordRequestDTO;
import org.funding.keyword.service.KeywordService;
import org.funding.keyword.vo.KeywordVO;
import org.funding.security.util.Auth;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "키워드 API")
@RestController
@RequestMapping("/api/keyword")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @ApiOperation(value = "키워드 단건 조회", notes = "이름을 기준으로 특정 키워드 정보를 조회합니다.")
    @Auth
    @GetMapping("/{name}")
    public ResponseEntity<KeywordVO> getKeyword(
            @ApiParam(value = "조회할 키워드 이름", required = true, example = "운동") @PathVariable("name") String name,
            HttpServletRequest request) {
        KeywordVO keyword = keywordService.findKeyword(name);
        return new ResponseEntity<>(keyword, HttpStatus.OK);
    }

    @ApiOperation(value = "전체 키워드 목록 조회", notes = "시스템에 등록된 모든 키워드 목록을 조회합니다.")
    @GetMapping("")
    public ResponseEntity<List<KeywordVO>> getAllKeywords() {
        List<KeywordVO> allKeywords = keywordService.findAllKeywords();
        return new ResponseEntity<>(allKeywords, HttpStatus.OK);
    }

    @ApiOperation(value = "카테고리별 키워드 목록 조회", notes = "특정 카테고리에 속한 모든 키워드 목록을 조회합니다.")
    @Auth
    @GetMapping("/category")
    public ResponseEntity<List<KeywordVO>> getAllKeywordsByCategoryName(
            @ApiParam(value = "조회할 카테고리 이름", required = true, example = "건강") @RequestParam("categoryName") String categoryName,
            HttpServletRequest request) {
        List<KeywordVO> allKeywordsByCategory = keywordService.findAllKeywordsByCategoryName(categoryName);
        return new ResponseEntity<>(allKeywordsByCategory, HttpStatus.OK);
    }

    @ApiOperation(value = "키워드 생성 (관리자용)", notes = "새로운 키워드를 시스템에 등록합니다.")
    @Auth
    @PostMapping("")
    public ResponseEntity<String> createKeyword(@RequestBody KeywordRequestDTO requestDTO,
                                                HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        keywordService.addKeyword(requestDTO, userId);
        return ResponseEntity.ok("키워드 추가 성공");
    }

    @ApiOperation(value = "키워드 삭제 (관리자용)", notes = "이름을 기준으로 특정 키워드를 삭제합니다.")
    @Auth
    @DeleteMapping("")
    public ResponseEntity<String> deleteKeyword(
            @ApiParam(value = "삭제할 키워드 이름", required = true, example = "운동") @RequestParam String name,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        keywordService.deleteKeyword(name, userId);
        return ResponseEntity.ok("키워드 삭제 성공");
    }
}