package org.funding.projectKeyword.service;

import lombok.RequiredArgsConstructor;
import org.funding.projectKeyword.dao.ProjectKeywordDAO;
import org.funding.projectKeyword.dto.ProjectKeywordRequestDTO;
import org.funding.projectKeyword.vo.ProjectKeywordVO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectKeywordService {

    private final ProjectKeywordDAO projectKeywordDAO;

    public void mapProjectKeyword(ProjectKeywordRequestDTO requestDTO) {
        ProjectKeywordVO projectKeywordVO = projectKeywordDAO.selectProjectKeyword(requestDTO);

        if (projectKeywordVO != null) {
            // 이미 매핑되어 잇음
            return;
        }

        projectKeywordDAO.insertProjectKeyword(requestDTO);
    }

    public void unmapProjectKeyword(ProjectKeywordRequestDTO requestDTO) {
        ProjectKeywordVO projectKeywordVO = projectKeywordDAO.selectProjectKeyword(requestDTO);

        if (projectKeywordVO == null) {
            // 이미 매핑되어 있지 않음
            return;
        }

        projectKeywordDAO.deleteProjectKeyword(requestDTO);
    }
}
