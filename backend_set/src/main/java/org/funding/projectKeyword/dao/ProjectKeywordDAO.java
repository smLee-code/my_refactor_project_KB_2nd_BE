package org.funding.projectKeyword.dao;


import org.funding.projectKeyword.dto.ProjectKeywordRequestDTO;
import org.funding.projectKeyword.vo.ProjectKeywordVO;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectKeywordDAO {

    ProjectKeywordVO selectProjectKeyword(ProjectKeywordRequestDTO requestDTO);

    void insertProjectKeyword(ProjectKeywordRequestDTO requestDTO);

    void deleteProjectKeyword(ProjectKeywordRequestDTO requestDTO);
}
