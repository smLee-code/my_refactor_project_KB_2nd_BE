package org.funding.keyword.controller;

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

@RestController
@RequestMapping("/api/keyword")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @Auth
    @GetMapping("/{name}")
    public ResponseEntity<KeywordVO> getKeyword(@PathVariable("name") String name,
                                                HttpServletRequest request) {
        KeywordVO keyword = keywordService.findKeyword(name);
        return new ResponseEntity<>(keyword, HttpStatus.OK);
    }

    @Auth
    @GetMapping("")
    public ResponseEntity<List<KeywordVO>> getAllKeywords(HttpServletRequest request) {
        List<KeywordVO> allKeywords = keywordService.findAllKeywords();

        return new ResponseEntity<>(allKeywords, HttpStatus.OK);
    }

    @Auth
    @GetMapping("/category")
    public ResponseEntity<List<KeywordVO>> getAllKeywordsByCategoryName(@RequestParam("categoryName") String categoryName,
                                                                        HttpServletRequest request) {
        List<KeywordVO> allKeywordsByCategory = keywordService.findAllKeywordsByCategoryName(categoryName);

        return new ResponseEntity<>(allKeywordsByCategory, HttpStatus.OK);
    }

    @Auth
    @PostMapping("")
    public ResponseEntity<String> createKeyword(@RequestBody KeywordRequestDTO requestDTO,
                                                HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        keywordService.addKeyword(requestDTO, userId);
        return ResponseEntity.ok("키워드 추가 성공");
    }

    @Auth
    @DeleteMapping("")
    public ResponseEntity<String> deleteKeyword(@RequestParam String name,
                                                HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        keywordService.deleteKeyword(name, userId);
        return ResponseEntity.ok("키워드 삭제 성공");
    }
}
