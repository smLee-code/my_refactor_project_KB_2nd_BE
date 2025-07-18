package org.funding.project.service;

import lombok.RequiredArgsConstructor;
import org.funding.project.dao.ProjectDAO;
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

}