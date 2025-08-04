package org.funding.projectKeyword.service;

import lombok.RequiredArgsConstructor;
import org.funding.keyword.dao.KeywordDAO;
import org.funding.keyword.dto.KeywordResponseDTO;
import org.funding.keyword.vo.KeywordVO;
import org.funding.project.dao.ProjectDAO;
import org.funding.project.dto.response.ProjectResponseDTO;
import org.funding.projectKeyword.dao.ProjectKeywordDAO;
import org.funding.projectKeyword.dto.ProjectKeywordRequestDTO;
import org.funding.projectKeyword.vo.ProjectKeywordVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectKeywordService {

    private final ProjectKeywordDAO projectKeywordDAO;
    private final KeywordDAO keywordDAO;
    private final ProjectDAO projectDAO;

    public List<KeywordVO> findKeywordIdsByProjectId(Long projectId) {
        List<Long> keywordIdList = projectKeywordDAO.selectKeywordIdsByProjectId(projectId);

        return keywordIdList.stream().map(keywordDAO::selectKeywordById).toList();
    }

//    public List<Long> findProjectIdsByKeywordId(Long keywordId) {
//
//    }

    public void mapProjectKeyword(ProjectKeywordRequestDTO requestDTO) {
        ProjectKeywordVO projectKeywordVO = projectKeywordDAO.findProjectKeywordMapping(requestDTO);

        if (projectKeywordVO != null) {
            // 이미 매핑되어 잇음
            return;
        }

        projectKeywordDAO.insertProjectKeyword(requestDTO);
    }

    public void unmapProjectKeyword(ProjectKeywordRequestDTO requestDTO) {
        ProjectKeywordVO projectKeywordVO = projectKeywordDAO.findProjectKeywordMapping(requestDTO);

        if (projectKeywordVO == null) {
            // 이미 매핑되어 있지 않음
            return;
        }

        projectKeywordDAO.deleteProjectKeyword(requestDTO);
    }
}
