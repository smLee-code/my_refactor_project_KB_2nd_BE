package org.funding.project.service;

import lombok.RequiredArgsConstructor;
import org.funding.project.dao.ProjectDAO;
import org.funding.project.dto.response.ProjectListDTO;
import org.funding.project.dto.response.ProjectResponseDTO;
import org.funding.project.vo.ProjectVO;
import org.funding.project.dto.request.*;
import org.funding.project.dto.response.*;
import org.funding.project.vo.*;
import org.funding.project.vo.enumType.ProjectType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {


    private final ProjectDAO projectDAO;


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

    public List<ProjectListDTO> searchByType(String type) {
        return projectDAO.searchProjectsByType(type);
    }

    public List<ProjectListDTO> searchByKeyword(String keyword) {
        return projectDAO.searchProjectsByKeyword(keyword);
    }

    public List<ProjectListDTO> getAllProjects() {
        return projectDAO.getAllProjects();
    }

    @Transactional
    public ProjectResponseDTO createProject(CreateProjectRequestDTO createRequestDTO) {
        // 1. 공통 프로젝트 정보 매핑 및 삽입
        ProjectVO projectVO = createRequestDTO.toCommonVO();
        projectDAO.insertProject(projectVO); // 이 호출 후 projectVO에 projectId가 채워질 것으로 예상
        Long projectId = projectVO.getProjectId(); // 삽입된 프로젝트의 ID

        // 2. 리턴할 응답 객체 생성, 공통 정보 매핑 및 삽입
        ProjectResponseDTO responseDTO = new ProjectResponseDTO();
        responseDTO.setBasicInfo(projectVO);

        // 3. 프로젝트 타입에 따른 추가 정보 매핑 및 삽입
        switch (createRequestDTO.getProjectType()) {
            case Challenge:
                CreateChallengeProjectRequestDTO challengeRequestDTO = (CreateChallengeProjectRequestDTO) createRequestDTO;
                ChallengeProjectVO challengeVO = challengeRequestDTO.toChallengeVO();
                challengeVO.setProjectId(projectId);
                projectDAO.insertChallengeProject(challengeVO);
                responseDTO.setDetailInfo(challengeVO);
                break;

            case Donation:
                CreateDonationProjectRequestDTO donationRequestDTO = (CreateDonationProjectRequestDTO) createRequestDTO;
                DonationProjectVO donationVO = donationRequestDTO.toDonationVO();
                donationVO.setProjectId(projectId);
                projectDAO.insertDonationProject(donationVO);
                responseDTO.setDetailInfo(donationVO);
                break;

            case Loan:
                CreateLoanProjectRequestDTO loanRequestDTO = (CreateLoanProjectRequestDTO) createRequestDTO;
                LoanProjectVO loanVO = loanRequestDTO.toLoanVO();
                loanVO.setProjectId(projectId);
                projectDAO.insertLoanProject(loanVO);
                responseDTO.setDetailInfo(loanVO);
                break;

            case Savings:
                CreateSavingsProjectRequestDTO savingsRequestDTO = (CreateSavingsProjectRequestDTO) createRequestDTO;
                SavingsProjectVO savingsVO = savingsRequestDTO.toSavingsVO();
                savingsVO.setProjectId(projectId);
                projectDAO.insertSavingsProject(savingsVO);
                responseDTO.setDetailInfo(savingsVO);
                break;

            default:
                throw new IllegalArgumentException("지원하지 않는 프로젝트 타입입니다: " + createRequestDTO.getProjectType());
        }

        return responseDTO;
    }

    public void deleteProject(Long projectId) {
        ProjectVO projectVO = projectDAO.selectProjectById(projectId);

        switch (projectVO.getProjectType()) {
            case Savings:
                projectDAO.deleteSavingsProjectById(projectId);
                break;

            case Loan:
                projectDAO.deleteLoanProjectById(projectId);
                break;

            case Challenge:
                projectDAO.deleteChallengeProjectById(projectId);
                break;

            case Donation:
                projectDAO.deleteDonationProjectById(projectId);
                break;

            default:
                throw new IllegalArgumentException("지원하지 않는 프로젝트 타입입니다: " + projectVO.getProjectType());
        }

        projectDAO.deleteProjectById(projectId);
    }
}