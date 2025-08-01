package org.funding.userKeyword.dao;

import org.funding.userKeyword.dto.UserKeywordRequestDTO;
import org.funding.userKeyword.vo.UserKeywordVO;
import org.springframework.stereotype.Repository;

@Repository
public interface UserKeywordDAO {
    UserKeywordVO selectUserKeyword(UserKeywordRequestDTO requestDTO);

    void insertUserKeyword(UserKeywordRequestDTO requestDTO);

    void deleteUserKeyword(UserKeywordRequestDTO requestDTO);
}
