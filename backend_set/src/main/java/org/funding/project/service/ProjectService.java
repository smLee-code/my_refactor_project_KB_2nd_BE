package org.funding.project.service;

import lombok.RequiredArgsConstructor;
import org.funding.project.dao.ProjectDAO;
import org.funding.project.dto.request.*;
import org.funding.project.dto.response.*;
import org.funding.project.vo.*;
import org.funding.project.vo.enumType.ProjectType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    @Transactional
    public ProjectResponseDTO createProject(CreateProjectRequestDTO createRequestDTO) {
        // 1. 공통 프로젝트 정보 매핑 및 삽입
        ProjectVO projectVO = createRequestDTO.toCommonVO();
        projectDAO.insertProject(projectVO); // 이 호출 후 projectVO에 projectId가 채워질 것으로 예상
        Long projectId = projectVO.getProjectId(); // 삽입된 프로젝트의 ID

        // 2. 리턴할 응답 객체 생성, 공통 정보 매핑 및 삽입
        ProjectResponseDTO responseDTO = new ProjectResponseDTO();
        responseDTO.setBasicVO(projectVO);

        // 3. 프로젝트 타입에 따른 추가 정보 매핑 및 삽입
        switch (createRequestDTO.getProjectType()) {
            case Challenge:
                CreateChallengeProjectRequestDTO challengeRequestDTO = (CreateChallengeProjectRequestDTO) createRequestDTO;
                ChallengeProjectVO challengeVO = challengeRequestDTO.toChallengeVO();
                challengeVO.setProjectId(projectId);
                projectDAO.insertChallengeProject(challengeVO);
                responseDTO.setDetailVO(challengeVO);
                break;

            case Donation:
                CreateDonationProjectRequestDTO donationRequestDTO = (CreateDonationProjectRequestDTO) createRequestDTO;
                DonationProjectVO donationVO = donationRequestDTO.toDonationVO();
                donationVO.setProjectId(projectId);
                projectDAO.insertDonationProject(donationVO);
                responseDTO.setDetailVO(donationVO);
                break;

            case Loan:
                CreateLoanProjectRequestDTO loanRequestDTO = (CreateLoanProjectRequestDTO) createRequestDTO;
                LoanProjectVO loanVO = loanRequestDTO.toLoanVO();
                loanVO.setProjectId(projectId);
                projectDAO.insertLoanProject(loanVO);
                responseDTO.setDetailVO(loanVO);
                break;

            case Savings:
                CreateSavingsProjectRequestDTO savingsRequestDTO = (CreateSavingsProjectRequestDTO) createRequestDTO;
                SavingsProjectVO savingsVO = savingsRequestDTO.toSavingsVO();
                savingsVO.setProjectId(projectId);
                projectDAO.insertSavingsProject(savingsVO);
                responseDTO.setDetailVO(savingsVO);
                break;

            default:
                throw new IllegalArgumentException("지원하지 않는 프로젝트 타입입니다: " + createRequestDTO.getProjectType());
        }

        return responseDTO;
    }
}