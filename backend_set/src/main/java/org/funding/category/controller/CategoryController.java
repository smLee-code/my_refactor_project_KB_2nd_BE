package org.funding.category.controller;

import lombok.RequiredArgsConstructor;
import org.funding.category.dto.CategoryWithKeywordsResponseDTO;
import org.funding.category.dto.CategoryRequestDTO;
import org.funding.category.service.CategoryService;
import org.funding.category.vo.CategoryVO;
import org.funding.security.util.Auth;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Auth
    @GetMapping("")
    public ResponseEntity<List<CategoryVO>> getAllCategories(HttpServletRequest request) {
        List<CategoryVO> allCategories = categoryService.getAllCategories();
        return new ResponseEntity<>(allCategories, HttpStatus.OK);
    }

    @Auth
    @GetMapping("/all")
    public ResponseEntity<List<CategoryWithKeywordsResponseDTO>> getAllCategoriesWithKeywords(HttpServletRequest request) {
        List<CategoryWithKeywordsResponseDTO> allWithKeywords = categoryService.getAllWithKeywords();

        return new ResponseEntity<>(allWithKeywords, HttpStatus.OK);
    }

    @Auth
    @PostMapping("")
    public ResponseEntity<String> createCategory(@RequestBody CategoryRequestDTO requestDTO,
                                                 HttpServletRequest request) {
        categoryService.addCategory(requestDTO);
        return new ResponseEntity<>("카테고리 생성 완료", HttpStatus.OK);
    }

    @Auth
    @DeleteMapping("")
    public ResponseEntity<String> deleteCategory(@RequestBody String name,
                                                 HttpServletRequest request) {
        categoryService.deleteCategory(name);
        return new ResponseEntity<>("카테고리 삭제 완료", HttpStatus.OK);
    }
}
