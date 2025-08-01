package org.funding.projectKeyword.dao;


import org.funding.keyword.dto.KeywordResponseDTO;
import org.funding.projectKeyword.dto.ProjectKeywordRequestDTO;
import org.funding.projectKeyword.vo.ProjectKeywordVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectKeywordDAO {

    ProjectKeywordVO findProjectKeywordMapping(ProjectKeywordRequestDTO requestDTO);

    List<Long> selectKeywordIdsByProjectId(Long projectId);

    void insertProjectKeyword(ProjectKeywordRequestDTO requestDTO);

    void deleteProjectKeyword(ProjectKeywordRequestDTO requestDTO);
}
