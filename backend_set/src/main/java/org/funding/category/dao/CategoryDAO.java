package org.funding.category.dao;

import org.funding.category.dto.CategoryRequestDTO;
import org.funding.category.dto.CategoryResponseDTO;
import org.funding.category.vo.CategoryVO;

import java.util.List;

public interface CategoryDAO {

    // 카테고리 이름으로 정보 조회
    CategoryResponseDTO selectCategory(String name);

    // id로 카테고리 조회
    CategoryVO selectCategoryById(Long categoryId);

    // 전체 카테고리 조회
    List<CategoryVO> selectAllCategories();

    // 카테고리 생성
    void insertCategory(CategoryRequestDTO requestDTO);

    // 카테고리 삭제
    void deleteCategory(String name);



}
