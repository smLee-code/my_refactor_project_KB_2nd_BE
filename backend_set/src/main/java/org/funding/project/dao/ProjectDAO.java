package org.funding.project.dao;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.funding.project.dto.ProjectDTO;
import org.funding.project.vo.ProjectVO;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectDAO {

    ProjectVO selectProjectById(Long projectId);

//    private final SqlSession sqlSession;
//    private static final String NAMESPACE = "org.funding.project.mapper.ProjectMapper.";

//    public java.util.List<org.funding.project.vo.ProjectVO> findAll() {
//        return sqlSession.selectList(NAMESPACE + "findAll");
//    }



}