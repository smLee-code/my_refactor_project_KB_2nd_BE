package org.funding.keyword.dao;

import org.funding.keyword.dto.KeywordRequestDTO;
import org.funding.keyword.vo.KeywordVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordDAO {

    KeywordVO selectKeywordById(Long keywordId);

    KeywordVO selectKeywordByName(String name);

    List<KeywordVO> selectAllKeywords();

    List<KeywordVO> selectKeywordsByCategoryName(String categoryName);

    List<KeywordVO> selectKeywordsByCategoryId(Long categoryId);

    public void insertKeyword(KeywordRequestDTO requestDTO);

    void deleteKeyword(String name);

    List<Long> selectKeywordIdsByUserId(Long userId);
}
