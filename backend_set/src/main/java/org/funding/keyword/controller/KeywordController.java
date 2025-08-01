package org.funding.keyword.controller;

import lombok.RequiredArgsConstructor;
import org.funding.keyword.dto.KeywordRequestDTO;
import org.funding.keyword.dto.KeywordResponseDTO;
import org.funding.keyword.service.KeywordService;
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
    public ResponseEntity<KeywordResponseDTO> getKeyword(@PathVariable("name") String name) {
        KeywordResponseDTO keyword = keywordService.findKeyword(name);

        return new ResponseEntity<>(keyword, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<KeywordResponseDTO>> getAllKeywords(@RequestParam("categoryName") String categoryName) {
        List<KeywordResponseDTO> allKeywords = keywordService.findAllKeywords(categoryName);

        return new ResponseEntity<>(allKeywords, HttpStatus.OK);
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
