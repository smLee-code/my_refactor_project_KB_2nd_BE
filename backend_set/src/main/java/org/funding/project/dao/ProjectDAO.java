package org.funding.project.dao;

import org.funding.project.dto.response.ProjectResponseDTO;
import org.funding.project.vo.*;
import org.springframework.stereotype.Repository;

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

}