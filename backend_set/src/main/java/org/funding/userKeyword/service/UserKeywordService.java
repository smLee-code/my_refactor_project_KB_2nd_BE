package org.funding.userKeyword.service;

import lombok.RequiredArgsConstructor;
import org.funding.projectKeyword.dto.ProjectKeywordRequestDTO;
import org.funding.projectKeyword.vo.ProjectKeywordVO;
import org.funding.userKeyword.dao.UserKeywordDAO;
import org.funding.userKeyword.dto.UserKeywordRequestDTO;
import org.funding.userKeyword.vo.UserKeywordVO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserKeywordService {

    private final UserKeywordDAO userKeywordDAO;

    public void mapUserKeyword(UserKeywordRequestDTO requestDTO) {
        UserKeywordVO userKeywordVO = userKeywordDAO.selectUserKeyword(requestDTO);

        if (userKeywordVO != null) {
            // 이미 매핑되어 잇음
            return;
        }

        userKeywordDAO.insertUserKeyword(requestDTO);
    }

    public void unmapProjectKeyword(UserKeywordRequestDTO requestDTO) {
        UserKeywordVO userKeywordVO = userKeywordDAO.selectUserKeyword(requestDTO);

        if (userKeywordVO == null) {
            // 이미 매핑되어 있지 않음
            return;
        }

        userKeywordDAO.deleteUserKeyword(requestDTO);
    }
}
