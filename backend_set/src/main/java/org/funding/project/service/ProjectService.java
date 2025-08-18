package org.funding.project.service;

import lombok.RequiredArgsConstructor;
import org.funding.S3.dao.S3ImageDAO;
import org.funding.S3.service.S3ImageService;
import org.funding.S3.vo.S3ImageVO;
import org.funding.S3.vo.enumType.ImageType;
import org.funding.fund.dto.FundListResponseDTO;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.ProjectException;
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
            throw new ProjectException(ErrorCode.PROJECT_NOT_FOUND);
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
                throw new ProjectException(ErrorCode.NOT_FOUND_PROJECT_TYPE);
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

//        List<Long> projectIds = projectList.stream()
//                .map(ProjectListDTO::getProjectId)
//                .collect(Collectors.toList());
//
//        List<S3ImageVO> allImages = s3ImageDAO.findImagesForPostIds(ImageType.Project, projectIds);
//        Map<Long, List<S3ImageVO>> imagesByProjectId = allImages.stream()
//                .collect(Collectors.groupingBy(S3ImageVO::getPostId));
//
//        for (ProjectListDTO project : projectList) {
//            project.setImages(imagesByProjectId.getOrDefault(project.getProjectId(), Collections.emptyList()));
//        }

        findImagesOfProject(projectList);

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

        findImagesOfProject(projectList);

        return projectList;
    }

    public List<ProjectListDTO> searchByKeyword(String keyword) {
        List<ProjectListDTO> projectList = projectDAO.searchProjectsByKeyword(keyword);

//        // 프로젝트 썸네일 이미지 추출
//        for (ProjectListDTO project : projectList) {
//            Long projectId = project.getProjectId();
//            S3ImageVO image = s3ImageService.getFirstImageForPost(ImageType.Project, projectId);
//            project.setThumbnailImage(image);
//        }

        findImagesOfProject(projectList);

        return projectList;
    }

    public List<ProjectListDTO> getAllProjects() {
        List<ProjectListDTO> projectList = projectDAO.getAllProjects();

//        // 프로젝트 썸네일 이미지 추출
//        for (ProjectListDTO project : projectList) {
//            Long projectId = project.getProjectId();
//            S3ImageVO image = s3ImageService.getFirstImageForPost(ImageType.Project, projectId);
//            project.setThumbnailImage(image);
//        }

        findImagesOfProject(projectList);

        return projectList;
    }

    public List<ProjectListDTO> getRelatedProjects(Long projectId) {
        // 0) 입력값 방어
        if (projectId == null) return Collections.emptyList();

        // 1) 기준 프로젝트 조회 (null-safe)
        var baseVo = projectDAO.selectProjectById(projectId);
        if (baseVo == null) return Collections.emptyList();

        ProjectListDTO baseProject = ProjectListDTO.fromVO(baseVo);
        if (baseProject == null) return Collections.emptyList();

        // 2) 같은 타입 프로젝트 조회 (null-safe)
        String typeStr = baseProject.getProjectType() == null
                ? null
                : String.valueOf(baseProject.getProjectType());

        List<ProjectListDTO> sameTypeProjects = Optional
                .ofNullable(projectDAO.searchProjectsByType(typeStr))
                .orElse(Collections.emptyList());

        // ProjectListDTO에 썸네일 이미지 쿼리하여 추가
        findImagesOfProject(sameTypeProjects);

        // 3) 기준 프로젝트 키워드 → Set<Long> (null 요소/ID 제거)
        final Set<Long> baseKeywordIds = Optional
                .ofNullable(projectKeywordService.findKeywordsByProjectId(baseProject.getProjectId()))
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)                  // null KeywordVO 제거
                .map(KeywordVO::getKeywordId)
                .filter(Objects::nonNull)                  // null keywordId 제거
                .collect(Collectors.toSet());

        // 기준 키워드가 없으면 같은 타입에서 자기 자신 제외 후 최대 4개 반환
        if (baseKeywordIds.isEmpty()) {
            return sameTypeProjects.stream()
                    .filter(Objects::nonNull)
                    .filter(p -> !Objects.equals(p.getProjectId(), projectId))
                    .limit(4)
                    .collect(Collectors.toList());
        }

        // 4) 프로젝트별 일치 키워드 개수 집계
        Map<ProjectListDTO, Long> projectMatchCounts = new HashMap<>();

        for (ProjectListDTO project : sameTypeProjects) {
            if (project == null) continue;

            Long pid = project.getProjectId();
            if (pid == null || Objects.equals(pid, projectId)) continue; // 자기 자신/ID 없는 프로젝트 제외

            List<KeywordVO> projectKeywords = projectKeywordService.findKeywordsByProjectId(pid);
            if (projectKeywords == null || projectKeywords.isEmpty()) continue;

            long matchCount = projectKeywords.stream()
                    .filter(Objects::nonNull)             // null KeywordVO 방어
                    .map(KeywordVO::getKeywordId)
                    .filter(Objects::nonNull)             // null keywordId 방어
                    .filter(baseKeywordIds::contains)     // Set 사용으로 O(1) contains
                    .count();

            if (matchCount > 0) {
                projectMatchCounts.put(project, matchCount);
            }
        }

        // 5) 일치 개수 내림차순 정렬 후 최대 4개 반환
        return projectMatchCounts.entrySet().stream()
                .sorted(Map.Entry.<ProjectListDTO, Long>comparingByValue(Comparator.reverseOrder()))
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
                throw new ProjectException(ErrorCode.NOT_PROJECT_TYPE);
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
            throw new ProjectException(ErrorCode.NOT_PROJECT_OWNER);
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
                throw new ProjectException(ErrorCode.NOT_PROJECT_TYPE);
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

    public void findImagesOfProject(List<ProjectListDTO> projectList) {

        for (ProjectListDTO project : projectList) {
            Long projectId = project.getProjectId();

            List<S3ImageVO> allImages = s3ImageService.getImagesForPost(ImageType.Project, projectId);

            if (allImages == null || allImages.size() == 0) {
                project.setImages(Collections.emptyList());
                project.setThumbnailImage(null);
                project.setThumbnailUrl("");
            } else {
                project.setImages(allImages);
                project.setThumbnailImage(allImages.get(0));
                project.setThumbnailUrl(allImages.get(0).getImageUrl());
            }

        }
    }

    public List<ProjectListDTO> recommendProjectsByUserKeywords(Long userId) {

        List<ProjectListDTO> projectList = projectKeywordService.recommendProjectsByUserKeywords(userId);

        if (userId == null) {
            // 비 로그인 시 -> 투표 여부 항상 false로 처리
            projectList.forEach(project -> project.setIsLiked(false));
        }
        else {
            projectList.forEach(project -> {
                Boolean isLiked = votesService.hasVoted(new VotesRequestDTO(userId, project.getProjectId()));
                project.setIsLiked(isLiked);
            });
        }



        findImagesOfProject(projectList);

        return projectList;

    }
}