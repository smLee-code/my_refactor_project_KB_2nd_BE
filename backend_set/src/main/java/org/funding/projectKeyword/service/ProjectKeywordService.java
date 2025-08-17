package org.funding.projectKeyword.service;

import lombok.RequiredArgsConstructor;
import org.funding.S3.dao.S3ImageDAO;
import org.funding.S3.vo.S3ImageVO;
import org.funding.S3.vo.enumType.ImageType;
import org.funding.category.dao.CategoryDAO;
import org.funding.category.vo.CategoryVO;
import org.funding.keyword.dao.KeywordDAO;
import org.funding.keyword.vo.KeywordVO;
import org.funding.project.dao.ProjectDAO;
import org.funding.project.dto.response.ProjectListDTO;
import org.funding.projectKeyword.dao.ProjectKeywordDAO;
import org.funding.projectKeyword.dto.ProjectKeywordRequestDTO;
import org.funding.projectKeyword.vo.ProjectKeywordVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class ProjectKeywordService {

    private final ProjectKeywordDAO projectKeywordDAO;
    private final KeywordDAO keywordDAO;
    private final ProjectDAO projectDAO;
    private final S3ImageDAO s3ImageDAO;
    private final CategoryDAO categoryDAO;

    public List<KeywordVO> findKeywordsByProjectId(Long projectId) {
        List<Long> keywordIdList = projectKeywordDAO.selectKeywordIdsByProjectId(projectId);

        return keywordIdList.stream().map(keywordDAO::selectKeywordById).toList();
    }

//    public List<Long> findProjectIdsByKeywordId(Long keywordId) {
//
//    }

    public void mapProjectKeyword(ProjectKeywordRequestDTO requestDTO) {
        ProjectKeywordVO projectKeywordVO = projectKeywordDAO.findProjectKeywordMapping(requestDTO);

        if (projectKeywordVO != null) {
            // 이미 매핑되어 잇음
            return;
        }

        projectKeywordDAO.insertProjectKeyword(requestDTO);
    }

    public void unmapProjectKeyword(ProjectKeywordRequestDTO requestDTO) {
        ProjectKeywordVO projectKeywordVO = projectKeywordDAO.findProjectKeywordMapping(requestDTO);

        if (projectKeywordVO == null) {
            // 이미 매핑되어 있지 않음
            return;
        }

        projectKeywordDAO.deleteProjectKeyword(requestDTO);
    }

    public List<ProjectListDTO> recommendProjectsByUserKeywords(Long userId) {
        // 1. 사용자 관심 키워드 목록 조회
        List<Long> keywordIds = keywordDAO.selectKeywordIdsByUserId(userId);
        if (keywordIds == null || keywordIds.isEmpty()) {
            return List.of(); // 관심 키워드가 없다면 빈 리스트 반환
        }

        // 각 키워드를 가중치에 따라 비율 설정 (A : B : C = 2 : 1 : 1 등)
        List<Integer> keywordWeights =
                keywordIds.stream().map(keywordId -> {
                    KeywordVO keywordVO = keywordDAO.selectKeywordById(keywordId);
                    CategoryVO categoryVO = categoryDAO.selectCategoryById(keywordVO.getCategoryId());
                    return categoryVO.getWeight();
                }).toList();

        double totalSum = keywordWeights.stream().mapToInt(Integer::intValue).sum();

        List<Integer> keywordRates =
                keywordWeights.stream()
                        .map(weight -> (weight / totalSum) * 4)
                        .map(Math::round)
                        .map(Long::intValue)
                .toList();

        Map<Long, Integer> keywordRateMap = IntStream.range(0, keywordIds.size())
                .boxed()
                .collect(Collectors.toMap(
                        i -> keywordIds.get(i),   // key: keywordId
                        i -> keywordRates.get(i)  // value: keywordRate
                ));

        // 각 키워드를 가지는 프로젝트를 해당 비율에 따라 조회 (최대 4개)
        List<Long> projectIds = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : keywordRateMap.entrySet()) {
            Long keywordId = entry.getKey();
            Integer keywordRate = entry.getValue();

            List<Long> projectIdsByKeywordId = projectKeywordDAO.selectKeywordIdsByProjectId(keywordId);

            List<Long> limitedProjectIds = projectIdsByKeywordId.stream()
                    .limit(keywordRate)  // rate 개수만큼 제한
                    .toList();

            projectIds.addAll(limitedProjectIds);
        }

        projectIds = projectIds.stream()
                .limit(4)  // 혹시 넘칠까봐 4개로 컷
                .toList();

        // 2. 관심 키워드에 연결된 프로젝트 ID 목록 조회
//        List<Long> projectIds = projectKeywordDAO.selectProjectIdsByKeywordIds(keywordIds);
//        if (projectIds == null || projectIds.isEmpty()) {
//            return List.of(); // 매칭되는 프로젝트 없음
//        }


        // 3. 프로젝트 정보 조회
        List<ProjectListDTO> projectDTOList = projectDAO.selectProjectsByIds(projectIds);// resultType: ProjectListDTO

        List<S3ImageVO> allImages = s3ImageDAO.findImagesForPostIds(ImageType.Project, projectIds);
        Map<Long, List<S3ImageVO>> imagesByProjectId = allImages.stream()
                .collect(Collectors.groupingBy(S3ImageVO::getPostId));

        for (ProjectListDTO project : projectDTOList) {
            project.setImages(imagesByProjectId.getOrDefault(project.getProjectId(), emptyList()));
        }


        return projectDTOList;
    }

}
