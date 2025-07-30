package org.funding.keyword.service;

import lombok.RequiredArgsConstructor;
import org.funding.keyword.dao.KeywordDAO;
import org.funding.keyword.dto.KeywordRequestDTO;
import org.funding.keyword.dto.KeywordResponseDTO;
import org.funding.keyword.vo.KeywordVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordDAO keywordDAO;

    public KeywordResponseDTO findKeyword(String name) {
        return keywordDAO.selectKeyword(name);
    }

    public List<KeywordResponseDTO> findAllKeywords(String categoryName) {
        return keywordDAO.selectKeywordsByCategoryName(categoryName);
    }

    public void addKeyword(KeywordRequestDTO requestDTO) {
        keywordDAO.insertKeyword(requestDTO);
    }

    public void deleteKeyword(String name) {
        keywordDAO.deleteKeyword(name);
    }
}
