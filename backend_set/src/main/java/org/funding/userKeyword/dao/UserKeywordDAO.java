package org.funding.userKeyword.dao;

import org.funding.userKeyword.dto.UserKeywordRequestDTO;
import org.funding.userKeyword.vo.UserKeywordVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserKeywordDAO {
    UserKeywordVO selectUserKeyword(UserKeywordRequestDTO requestDTO);

    List<Long> selectKeywordIdsByUserId(Long userId);

    void insertUserKeyword(UserKeywordRequestDTO requestDTO);

    void deleteUserKeyword(UserKeywordRequestDTO requestDTO);

    void deleteKeywordsByUserId(Long userId);
}
