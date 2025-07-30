package org.funding.keyword.dao;

import org.funding.keyword.dto.KeywordRequestDTO;
import org.funding.keyword.dto.KeywordResponseDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordDAO {

    KeywordResponseDTO selectKeyword(String name);

    List<KeywordResponseDTO> selectKeywordsByCategoryName(String categoryName);

    public void insertKeyword(KeywordRequestDTO requestDTO);

    void deleteKeyword(String name);


}
