package org.funding.category.dao;

import org.funding.category.dto.CategoryRequestDTO;
import org.funding.category.dto.CategoryResponseDTO;
import org.funding.category.vo.CategoryVO;

import java.util.List;

public interface CategoryDAO {

    CategoryResponseDTO selectCategory(String name);

    CategoryVO selectCategoryById(Long categoryId);

    List<CategoryVO> selectAllCategories();



    void insertCategory(CategoryRequestDTO requestDTO);

    void deleteCategory(String name);



}
