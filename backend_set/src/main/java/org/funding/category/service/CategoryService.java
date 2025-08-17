package org.funding.category.service;

import lombok.RequiredArgsConstructor;
import org.funding.category.dao.CategoryDAO;
import org.funding.category.dto.CategoryIdAndNameDTO;
import org.funding.category.dto.CategoryRequestDTO;
import org.funding.category.dto.CategoryWithKeywordsResponseDTO;
import org.funding.category.vo.CategoryVO;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.CategoryException;
import org.funding.keyword.dto.KeywordIdAndNameDTO;
import org.funding.keyword.service.KeywordService;
import org.funding.keyword.vo.KeywordVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final KeywordService keywordService;

    private final CategoryDAO categoryDAO;

    public List<CategoryVO> getAllCategories() {
        return categoryDAO.selectAllCategories();
    }

    public List<CategoryWithKeywordsResponseDTO> getAllWithKeywords() {
        List<CategoryIdAndNameDTO> categoryList =
                getAllCategories().stream()
                .map(CategoryIdAndNameDTO::fromVO)
                .toList();

        return categoryList.stream()
                .map(category -> {
                    List<KeywordVO> keywords = keywordService.findAllKeywordsByCategoryId(category.getId());

                    List<KeywordIdAndNameDTO> keywordList = keywords.stream()
                            .map(KeywordIdAndNameDTO::fromVO)
                            .toList();

                    return CategoryWithKeywordsResponseDTO.from(category, keywordList);
                })
                .toList();
    }

    public void addCategory(CategoryRequestDTO requestDTO) {

        if (categoryDAO.selectCategory(requestDTO.getName()) != null) {
            throw new CategoryException(ErrorCode.SAME_CATEGORY_NAME);
        }

        categoryDAO.insertCategory(requestDTO);
    }

    public void deleteCategory(String name) {
        if (categoryDAO.selectCategory(name) == null) {
            throw new CategoryException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        categoryDAO.deleteCategory(name);
    }

}
