package org.funding.category.controller;

import lombok.RequiredArgsConstructor;
import org.funding.category.dto.CategoryRequestDTO;
import org.funding.category.dto.CategoryResponseDTO;
import org.funding.category.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<CategoryResponseDTO> allCategories = categoryService.getAllCategories();
        return new ResponseEntity<>(allCategories, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<String> createCategory(@RequestBody CategoryRequestDTO requestDTO) {
        categoryService.addCategory(requestDTO);

        return new ResponseEntity<>("카테고리 생성 완료", HttpStatus.OK);
    }

    @DeleteMapping("")
    public ResponseEntity<String> deleteCategory(@RequestBody String name) {
        categoryService.deleteCategory(name);

        return new ResponseEntity<>("카테고리 삭제 완료", HttpStatus.OK);
    }
}
