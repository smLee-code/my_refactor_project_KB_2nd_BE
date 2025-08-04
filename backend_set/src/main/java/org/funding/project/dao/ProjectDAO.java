package org.funding.project.dao;

import org.funding.project.dto.response.ProjectListDTO;
import org.funding.project.dto.response.TopProjectDTO;
import org.funding.project.vo.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectDAO {

    ProjectVO selectProjectById(Long projectId);
    SavingsProjectVO selectSavingByProjectId(Long projectId);
    LoanProjectVO selectLoanByProjectId(Long projectId);
    ChallengeProjectVO selectChallengeByProjectId(Long projectId);
    DonationProjectVO selectDonationByProjectId(Long projectId);


//    private final SqlSession sqlSession;
//    private static final String NAMESPACE = "org.funding.project.mapper.ProjectMapper.";

    void insertProject(ProjectVO projectVO);

    void insertSavingsProject(SavingsProjectVO savingProjectVO);

    void insertLoanProject(LoanProjectVO loanProjectVO);

    void insertDonationProject(DonationProjectVO donationProjectVO);

    void insertChallengeProject(ChallengeProjectVO challengeProjectVO);


    List<ProjectListDTO> getAllProjects();

    List<ProjectListDTO> searchProjectsByType(String type);

    List<ProjectListDTO> searchProjectsByKeyword(String keyword);

    List<TopProjectDTO> getTopProjects();

    void deleteProjectById(Long projectId);

    void deleteSavingsProjectById(Long projectId);

    void deleteLoanProjectById(Long projectId);

    void deleteChallengeProjectById(Long projectId);

    void deleteDonationProjectById(Long projectId);

    List<ProjectVO> findByUserId(Long userId); // 유저 프로젝트 조회

    int countByUserId(Long userId); // 유저 프로젝트 개수 조회

    ProjectVO findById(Long projectId); // 프로젝트 조회

}