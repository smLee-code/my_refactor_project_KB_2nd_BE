package org.funding.project.dao;

import org.funding.project.dto.response.ProjectDetailDTO;
import org.funding.project.dto.response.ProjectListDTO;
import org.funding.project.dto.response.TopProjectDTO;
import org.funding.project.vo.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ProjectDAO {

    ProjectVO findProjectById(Long projectId);

    SavingsProjectVO findSavingsProjectById(Long projectId);

    LoanProjectVO findLoanProjectById(Long projectId);

    ChallengeProjectVO findChallengeProjectById(Long projectId);

    DonationProjectVO findDonationProjectById(Long projectId);

    void insertProject(ProjectVO projectVO);

    void insertSavingsProject(SavingsProjectVO savingProjectVO);

    void insertLoanProject(LoanProjectVO loanProjectVO);

    void insertDonationProject(DonationProjectVO donationProjectVO);

    void insertChallengeProject(ChallengeProjectVO challengeProjectVO);

    List<Long> findAllIds();

    List<ProjectListDTO> findAll();

    List<ProjectListDTO> getAllProjectsWithDetails(Long loginUserId);

    List<ProjectListDTO> searchProjectsByType(String type);

    List<ProjectListDTO> searchProjectsByKeyword(String keyword);

    List<TopProjectDTO> getTopProjects();

    List<Map<String, Object>> getProjectTypeDistribution();

    void deleteProjectById(Long projectId);

    void deleteSavingsProjectById(Long projectId);

    void deleteLoanProjectById(Long projectId);

    void deleteChallengeProjectById(Long projectId);

    void deleteDonationProjectById(Long projectId);

    List<ProjectVO> findByUserId(Long userId); // 유저 프로젝트 조회

    int countByUserId(Long userId); // 유저 프로젝트 개수 조회

    List<Map<String, Object>> getProjectTrends();

    List<ProjectListDTO> getProjectsByKeyword(Long userId);

    List<ProjectListDTO> selectProjectsByIds(List<Long> projectIds);

    ProjectDetailDTO selectProjectDetailById(Long projectId);
}