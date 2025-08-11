package org.funding.project.service;

import lombok.RequiredArgsConstructor;
import org.funding.S3.dao.S3ImageDAO;
import org.funding.S3.service.S3ImageService;
import org.funding.S3.vo.S3ImageVO;
import org.funding.S3.vo.enumType.ImageType;
import org.funding.fund.dto.FundListResponseDTO;
import org.funding.keyword.vo.KeywordVO;
import org.funding.project.dao.ProjectDAO;
import org.funding.project.dto.response.ProjectListDTO;
import org.funding.project.dto.response.ProjectResponseDTO;
import org.funding.project.vo.ProjectVO;
import org.funding.project.dto.request.*;
import org.funding.project.dto.response.*;
import org.funding.project.vo.*;
import org.funding.projectKeyword.dto.ProjectKeywordRequestDTO;
import org.funding.projectKeyword.service.ProjectKeywordService;
import org.funding.user.service.MyPageService;
import org.funding.votes.dao.VotesDAO;
import org.funding.votes.dto.VotesRequestDTO;
import org.funding.votes.service.VotesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final MyPageService myPageService;
    private final ProjectKeywordService projectKeywordService;
    private final VotesService votesService;

    private final ProjectDAO projectDAO;
    private final VotesDAO votesDAO;
    private final S3ImageService s3ImageService;
    private final S3ImageDAO s3ImageDAO;


    public List<TopProjectDTO> getTopProjects() {
        List<TopProjectDTO> topProjects = projectDAO.getTopProjects();

        if (topProjects == null || topProjects.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> projectIds = topProjects.stream()
                .map(TopProjectDTO::getProjectId)
                .collect(Collectors.toList());

        List<S3ImageVO> allImages = s3ImageDAO.findImagesForPostIds(ImageType.Project, projectIds);

        Map<Long, List<S3ImageVO>> imagesByProjectId = allImages.stream()
                .collect(Collectors.groupingBy(S3ImageVO::getPostId));

        for (TopProjectDTO project : topProjects) {
            project.setImages(imagesByProjectId.getOrDefault(project.getProjectId(), Collections.emptyList()));
        }

        return topProjects;
    }

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

        Long voteCount = votesDAO.countVotes(projectId);
        dto.setVoteCount(voteCount);

        // 프로젝트 이미지 추출
        List<S3ImageVO> images = s3ImageService.getImagesForPost(ImageType.Project, projectId);
        dto.setImageList(images);

        return dto;
    }

    public List<ProjectListDTO> getProjectWithDetailList(String keyword, String type, Long loginUserId) {
        // 1. 프로젝트 기본 정보 목록 조회 (1차 쿼리)
        List<ProjectListDTO> projectList;
        if (type != null && !type.isEmpty()) {
            projectList = projectDAO.searchProjectsByType(type);
        } else if (keyword != null && !keyword.isEmpty()) {
            projectList = projectDAO.searchProjectsByKeyword(keyword);
        } else {
            projectList = projectDAO.getAllProjects();
        }

        if (projectList == null || projectList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> projectIds = projectList.stream()
                .map(ProjectListDTO::getProjectId)
                .collect(Collectors.toList());

        List<S3ImageVO> allImages = s3ImageDAO.findImagesForPostIds(ImageType.Project, projectIds);
        Map<Long, List<S3ImageVO>> imagesByProjectId = allImages.stream()
                .collect(Collectors.groupingBy(S3ImageVO::getPostId));

        for (ProjectListDTO project : projectList) {
            project.setImages(imagesByProjectId.getOrDefault(project.getProjectId(), Collections.emptyList()));
        }

        if (loginUserId == null) {
            // 비 로그인 시 -> 투표 여부 항상 false로 처리
            projectList.forEach(project -> project.setIsLiked(false));
        }
        else {
            projectList.forEach(project -> {
                Boolean isLiked = votesService.hasVoted(new VotesRequestDTO(loginUserId, project.getProjectId()));
                project.setIsLiked(isLiked);
            });
        }

        return projectList;
    }

    public List<ProjectListDTO> searchByType(String type) {
        List<ProjectListDTO> projectList = projectDAO.searchProjectsByType(type);

        // 프로젝트 썸네일 이미지 추출
        for (ProjectListDTO project : projectList) {
            Long projectId = project.getProjectId();
            S3ImageVO image = s3ImageService.getFirstImageForPost(ImageType.Project, projectId);
            project.setThumbnailImage(image);
        }

        return projectList;
    }

    public List<ProjectListDTO> searchByKeyword(String keyword) {
        List<ProjectListDTO> projectList = projectDAO.searchProjectsByKeyword(keyword);

        // 프로젝트 썸네일 이미지 추출
        for (ProjectListDTO project : projectList) {
            Long projectId = project.getProjectId();
            S3ImageVO image = s3ImageService.getFirstImageForPost(ImageType.Project, projectId);
            project.setThumbnailImage(image);
        }

        return projectList;
    }

    public List<ProjectListDTO> getAllProjects() {
        List<ProjectListDTO> projectList = projectDAO.getAllProjects();

        // 프로젝트 썸네일 이미지 추출
        for (ProjectListDTO project : projectList) {
            Long projectId = project.getProjectId();
            S3ImageVO image = s3ImageService.getFirstImageForPost(ImageType.Project, projectId);
            project.setThumbnailImage(image);
        }

        return projectList;
    }

    public List<ProjectListDTO> getRelatedProjects(Long projectId) {

        ProjectListDTO baseProject = ProjectListDTO.fromVO(projectDAO.selectProjectById(projectId));
        List<KeywordVO> baseKeywords = projectKeywordService.findKeywordsByProjectId(baseProject.getProjectId());

        List<ProjectListDTO> sameTypeProjects = projectDAO.searchProjectsByType(String.valueOf(baseProject.getProjectType()));

        if (baseKeywords == null || baseKeywords.isEmpty()) { // baseKeywords가 null이거나 비어있으면

            // 같은 타입의 프로젝트 중 최대 4개만 반환
            return sameTypeProjects.stream()
                    .filter(p -> !p.getProjectId().equals(projectId))
                    .limit(4)
                    .collect(Collectors.toList());
        }

        // baseKeywords의 키워드 ID만 추출하여 Set으로 변환 (빠른 비교를 위함)

        List<Long> baseKeywordIds = baseKeywords.stream()
                .map(KeywordVO::getKeywordId)
                .toList();

        // 각 프로젝트별로 일치하는 키워드 개수를 저장할 맵
        Map<ProjectListDTO, Long> projectMatchCounts = new HashMap<>();

        for (ProjectListDTO project : sameTypeProjects) {
            if (project.getProjectId().equals(projectId)) {
                continue; // 자기 자신은 관련 프로젝트에서 제외
            }

            List<KeywordVO> projectKeywords = projectKeywordService.findKeywordsByProjectId(project.getProjectId());
            if (projectKeywords == null || projectKeywords.isEmpty()) {
                continue; // 키워드가 없는 프로젝트는 스킵
            }

            long matchCount = projectKeywords.stream()
                    .filter(keyword -> baseKeywordIds.contains(keyword.getKeywordId())) // KeywordVO에 getKeywordName() 메서드가 있다고 가정
                    .count();

            if (matchCount > 0) {
                projectMatchCounts.put(project, matchCount);
            }
        }

        // 일치하는 키워드 개수가 많은 순서대로 정렬하고, 최대 3개만 반환
        return projectMatchCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) // 일치 개수 내림차순 정렬
                .map(Map.Entry::getKey)
                .limit(4)
                .collect(Collectors.toList());
    }

    @Transactional

    public ProjectResponseDTO createProject(CreateProjectRequestDTO createRequestDTO, List<MultipartFile> images, Long userId) throws IOException {
        // 1. 공통 프로젝트 정보 매핑 및 삽입

        ProjectVO projectVO = createRequestDTO.toCommonVO();
        projectVO.setUserId(userId);
        projectDAO.insertProject(projectVO); // 이 호출 후 projectVO에 projectId가 채워질 것으로 예상
        Long projectId = projectVO.getProjectId(); // 삽입된 프로젝트의 ID


        // 등록한 이미지에 대해서 이미지 저장
        if (images != null && images.size() > 0) {
            s3ImageService.uploadImagesForPost(ImageType.Project, projectId, images);
        }

        // 2. 리턴할 응답 객체 생성, 공통 정보 매핑 및 삽입

        ProjectResponseDTO responseDTO = new ProjectResponseDTO();
        responseDTO.setBasicInfo(projectVO);

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

        List<ProjectKeywordRequestDTO> projectKeywordRequestList =
                createRequestDTO.getKeywordIds().stream()
                        .map(keywordId -> new ProjectKeywordRequestDTO(projectId, keywordId))
                        .toList();

        for (ProjectKeywordRequestDTO projectKeywordRequest : projectKeywordRequestList) {
            projectKeywordService.mapProjectKeyword(projectKeywordRequest);
        }

        // 각 키워드 ID를 프로젝트 ID와 맵핑 (project_keywords 테이블에 저장)
        createRequestDTO.getKeywordIds()
                .forEach(keywordId -> {
                    projectKeywordService.mapProjectKeyword(new ProjectKeywordRequestDTO(projectId, keywordId));
                });

        return responseDTO;
    }

    public void deleteProject(Long projectId, Long userId) {
        ProjectVO projectVO = projectDAO.selectProjectById(projectId);
        if (!Objects.equals(projectVO.getUserId(), userId)) {
            throw new RuntimeException("해당 프로젝트의 작성자가 아닙니다.");
        }
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

        // 프로젝트 관련 이미지 삭제
        s3ImageService.deleteImagesForPost(ImageType.Project, projectId);
    }

    public List<KeywordVO> getProjectKeywords(Long projectId) {
        List<KeywordVO> keywordDTOList = projectKeywordService.findKeywordsByProjectId(projectId);

        return keywordDTOList;
    }

    public void addKeywordIntoProject(ProjectKeywordRequestDTO requestDTO) {

        projectKeywordService.mapProjectKeyword(requestDTO);
    }

    public void deleteKeywordFromProject(ProjectKeywordRequestDTO requestDTO) {

        projectKeywordService.unmapProjectKeyword(requestDTO);
    }



    public List<Map<String, Object>> getProjectTypeDistribution() {
        List<Map<String, Object>> rawData = projectDAO.getProjectTypeDistribution();

        // 총합 계산
        int total = rawData.stream()
                .mapToInt(row -> ((Number) row.get("count")).intValue())
                .sum();

        // 순서가 유지되는 타입 목록 (한글명)
        Map<String, String> typeNameMap = new LinkedHashMap<>();
        typeNameMap.put("Savings", "저축");
        typeNameMap.put("Loan", "대출");
        typeNameMap.put("Challenge", "챌린지");
        typeNameMap.put("Donation", "기부");

        // 타입별 색상 매핑
        Map<String, String> typeColorMap = Map.of(
                "Savings", "#3B82F6",   // 파란색
                "Loan", "#10B981",      // 초록색
                "Challenge", "#F59E0B", // 주황색
                "Donation", "#8B5CF6"   // 보라색
        );

        List<Map<String, Object>> resultList = new ArrayList<>();

        typeNameMap.forEach((typeKey, typeName) -> {
            int count = rawData.stream()
                    .filter(row -> typeKey.equals(row.get("type")))
                    .mapToInt(row -> ((Number) row.get("count")).intValue())
                    .findFirst()
                    .orElse(0);

            Map<String, Object> result = new HashMap<>();
            result.put("label", typeName); // 한글명
            result.put("color", typeColorMap.get(typeKey)); // 색상
            result.put("value", total == 0 ? 0 : Math.round((double) count / total * 100));
            resultList.add(result);
        });

        return resultList;
    }

    public Map<String, List<Integer>> getProjectTrends() {
        List<Map<String, Object>> rawData = projectDAO.getProjectTrends();

        List<String> types = Arrays.asList("Savings", "Loan", "Donation", "Challenge");
        List<String> weeks = Arrays.asList("Week1", "Week2", "Week3", "Week4", "Week5");

        Map<String, List<Integer>> trends = new LinkedHashMap<>();
        types.forEach(type -> trends.put(type, new ArrayList<>(Collections.nCopies(5, 0))));

        for (Map<String, Object> row : rawData) {
            String type = (String) row.get("project_type");
            String week = (String) row.get("week_group");
            int count = ((Number) row.get("count")).intValue();

            int weekIndex = weeks.indexOf(week);
            if (weekIndex >= 0 && trends.containsKey(type)) {
                trends.get(type).set(weekIndex, count);
            }
        }

        return trends;
    }

    public List<ProjectListDTO> getProjectsByUserKeywords(long userId) {
        return projectDAO.getProjectsByKeyword(userId);
    }
}