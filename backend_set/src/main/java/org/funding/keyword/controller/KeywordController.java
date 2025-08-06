package org.funding.keyword.controller;

import lombok.RequiredArgsConstructor;
import org.funding.keyword.dto.KeywordRequestDTO;
import org.funding.keyword.service.KeywordService;
import org.funding.keyword.vo.KeywordVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/keyword")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @GetMapping("/{name}")
    public ResponseEntity<KeywordVO> getKeyword(@PathVariable("name") String name) {
        KeywordVO keyword = keywordService.findKeyword(name);

        return new ResponseEntity<>(keyword, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<KeywordVO>> getAllKeywords() {
        List<KeywordVO> allKeywords = keywordService.findAllKeywords();

        return new ResponseEntity<>(allKeywords, HttpStatus.OK);
    }

    @GetMapping("/category")
    public ResponseEntity<List<KeywordVO>> getAllKeywordsByCategoryName(@RequestParam("categoryName") String categoryName) {
        List<KeywordVO> allKeywordsByCategory = keywordService.findAllKeywordsByCategoryName(categoryName);

        return new ResponseEntity<>(allKeywordsByCategory, HttpStatus.OK);
    }


    @PostMapping("")
    public ResponseEntity<String> createKeyword(@RequestBody KeywordRequestDTO requestDTO) {
        keywordService.addKeyword(requestDTO);
        return ResponseEntity.ok("키워드 추가 성공");
    }

    @DeleteMapping("")
    public ResponseEntity<String> deleteKeyword(@RequestParam String name) {
        keywordService.deleteKeyword(name);
        return ResponseEntity.ok("키워드 삭제 성공");
    }
}
