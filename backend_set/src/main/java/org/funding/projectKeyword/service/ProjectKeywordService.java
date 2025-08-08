package org.funding.projectKeyword.service;

import lombok.RequiredArgsConstructor;
import org.funding.keyword.dao.KeywordDAO;
import org.funding.keyword.vo.KeywordVO;
import org.funding.project.dao.ProjectDAO;
import org.funding.project.dto.response.ProjectListDTO;
import org.funding.projectKeyword.dao.ProjectKeywordDAO;
import org.funding.projectKeyword.dto.ProjectKeywordRequestDTO;
import org.funding.projectKeyword.vo.ProjectKeywordVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectKeywordService {

    private final ProjectKeywordDAO projectKeywordDAO;
    private final KeywordDAO keywordDAO;
    private final ProjectDAO projectDAO;

    public List<KeywordVO> findKeywordsByProjectId(Long projectId) {
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

    public List<ProjectListDTO> recommendProjectsByUserKeywords(Long userId) {
        // 1. 사용자 관심 키워드 목록 조회
        List<Long> keywordIds = keywordDAO.selectKeywordIdsByUserId(userId);
        if (keywordIds == null || keywordIds.isEmpty()) {
            return List.of(); // 관심 키워드가 없다면 빈 리스트 반환
        }

        // 2. 관심 키워드에 연결된 프로젝트 ID 목록 조회
        List<Long> projectIds = projectKeywordDAO.selectProjectIdsByKeywordIds(keywordIds);
        if (projectIds == null || projectIds.isEmpty()) {
            return List.of(); // 매칭되는 프로젝트 없음
        }

        // 3. 프로젝트 정보 조회 및 반환
        return projectDAO.selectProjectsByIds(projectIds); // resultType: ProjectListDTO
    }

}
