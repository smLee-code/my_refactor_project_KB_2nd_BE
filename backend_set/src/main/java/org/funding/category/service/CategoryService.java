package org.funding.category.service;

import lombok.RequiredArgsConstructor;
import org.funding.category.dao.CategoryDAO;
import org.funding.category.dto.CategoryRequestDTO;
import org.funding.category.dto.CategoryResponseDTO;
import org.funding.exception.DuplicateCategoryException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryDAO categoryDAO;


    public List<CategoryResponseDTO> getAllCategories() {
        return categoryDAO.selectAllCategories();
    }

    public void addCategory(CategoryRequestDTO requestDTO) {

        if (categoryDAO.selectCategory(requestDTO.getName()) != null) {
            throw new DuplicateCategoryException("이미 존재하는 이름의 카테고리입니다.");
        }

        categoryDAO.insertCategory(requestDTO);
    }

    public void deleteCategory(String name) {

        if (categoryDAO.selectCategory(name) == null) {
            return;
        }

        categoryDAO.deleteCategory(name);
    }

}
