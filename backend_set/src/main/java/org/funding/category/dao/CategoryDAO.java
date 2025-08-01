package org.funding.category.dao;

import org.funding.category.dto.CategoryRequestDTO;
import org.funding.category.dto.CategoryResponseDTO;

import java.util.List;

public interface CategoryDAO {

    CategoryResponseDTO selectCategory(String name);

    List<CategoryResponseDTO> selectAllCategories();

    void insertCategory(CategoryRequestDTO requestDTO);

    void deleteCategory(String name);
    
}
