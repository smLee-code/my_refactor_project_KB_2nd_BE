package org.funding.keyword.service;

import lombok.RequiredArgsConstructor;
import org.funding.keyword.dao.KeywordDAO;
import org.funding.keyword.dto.KeywordRequestDTO;
import org.funding.keyword.vo.KeywordVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordDAO keywordDAO;

    public KeywordVO findKeyword(String name) {
        return keywordDAO.selectKeywordByName(name);
    }

    public List<KeywordVO> findAllKeywords() {
        return keywordDAO.selectAllKeywords();
    }

    public List<KeywordVO> findAllKeywordsByCategoryName(String categoryName) {
        return keywordDAO.selectKeywordsByCategoryName(categoryName);
    }

    public List<KeywordVO> findAllKeywordsByCategoryId(Long categoryId) {
        return keywordDAO.selectKeywordsByCategoryId(categoryId);
    }

    public void addKeyword(KeywordRequestDTO requestDTO) {
        keywordDAO.insertKeyword(requestDTO);
    }

    public void deleteKeyword(String name) {
        keywordDAO.deleteKeyword(name);
    }


}
