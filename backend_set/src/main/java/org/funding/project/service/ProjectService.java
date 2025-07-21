package org.funding.project.service;

import lombok.RequiredArgsConstructor;
import org.funding.project.dao.ProjectDAO;
import org.funding.project.dto.response.ProjectResponseDTO;
import org.funding.project.vo.ProjectVO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {


    private final ProjectDAO projectDAO;


    /**
     * 프로젝트 ID로 프로젝트를 조회합니다.
     *
     * @param projectId 조회할 프로젝트 ID
     * @return ProjectVO (조회된 프로젝트 정보)
     * @throws RuntimeException 프로젝트가 존재하지 않을 경우
     */

    public ProjectVO selectProjectById(Long projectId) {
        ProjectVO project = projectDAO.selectProjectById(projectId);
        if (project == null) {
            throw new RuntimeException("해당 ID의 프로젝트가 존재하지 않습니다.");
        }
        return project;
    }


    public ProjectResponseDTO getProjectDetails(Long projectId) {
        ProjectVO project = selectProjectById(projectId);

        Object detailInfo = null;

        switch (project.getProjectType()) {
            case Loan:
                detailInfo = projectDAO.selectLoanByProjectId(projectId);
                break;

            case Savings:
                detailInfo = projectDAO.selectSavingByProjectId(projectId);
                break;

            case Challenge:
                detailInfo = projectDAO.selectChallengeByProjectId(projectId);
                break;

            case Donation:
                detailInfo = projectDAO.selectDonationByProjectId(projectId);
                break;

            default:
                throw new RuntimeException("알 수 없는 프로젝트 타입입니다: " + project.getProjectType());
        }


        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setBasicInfo(project);
        dto.setDetailInfo(detailInfo);

        return dto;
    }



}