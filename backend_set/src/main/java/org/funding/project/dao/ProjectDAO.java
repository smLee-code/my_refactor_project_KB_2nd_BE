package org.funding.project.dao;

import org.funding.project.vo.*;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectDAO {
    ProjectVO selectProjectById(Long projectId);

    void insertProject(ProjectVO projectVO);

    void insertSavingsProject(SavingsProjectVO savingProjectVO);

    void insertLoanProject(LoanProjectVO loanProjectVO);

    void insertDonationProject(DonationProjectVO donationProjectVO);

    void insertChallengeProject(ChallengeProjectVO challengeProjectVO);
}