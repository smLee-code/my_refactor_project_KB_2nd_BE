package org.funding.category.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(tags = "카테고리 API") // 컨트롤러 대표 이름 설정
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @ApiOperation(value = "전체 카테고리 목록 조회", notes = "시스템에 등록된 모든 카테고리 목록을 조회합니다.")
    @GetMapping("")
    public ResponseEntity<List<CategoryVO>> getAllCategories() {
        List<CategoryVO> allCategories = categoryService.getAllCategories();
        return new ResponseEntity<>(allCategories, HttpStatus.OK);
    }

    @ApiOperation(value = "키워드를 포함한 전체 카테고리 목록 조회", notes = "각 카테고리에 속한 키워드들을 포함하여 전체 목록을 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity<List<CategoryWithKeywordsResponseDTO>> getAllCategoriesWithKeywords() {
        List<CategoryWithKeywordsResponseDTO> allWithKeywords = categoryService.getAllWithKeywords();
        return new ResponseEntity<>(allWithKeywords, HttpStatus.OK);
    }

    @ApiOperation(value = "카테고리 생성 (관리자용)", notes = "새로운 카테고리를 시스템에 등록합니다.")
    @PostMapping("")
    public ResponseEntity<String> createCategory(@RequestBody CategoryRequestDTO requestDTO) {
        categoryService.addCategory(requestDTO);
        return new ResponseEntity<>("카테고리 생성 완료", HttpStatus.OK);
    }

    @ApiOperation(value = "카테고리 삭제 (관리자용)", notes = "이름을 기준으로 특정 카테고리를 삭제합니다.")
    @DeleteMapping("")
    public ResponseEntity<String> deleteCategory(
            @ApiParam(value = "삭제할 카테고리 이름", required = true, example = "IT") @RequestBody String name) {
        categoryService.deleteCategory(name);
        return new ResponseEntity<>("카테고리 삭제 완료", HttpStatus.OK);
    }
}
