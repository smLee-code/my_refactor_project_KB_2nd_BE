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
import org.funding.project.vo.ProjectVO;
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
        // 사용자 관심 키워드 목록 조회
        List<Long> keywordIds = keywordDAO.selectKeywordIdsByUserId(userId);
        if (keywordIds == null || keywordIds.isEmpty()) {
            return List.of(); // 관심 키워드가 없다면 빈 리스트 반환
        }

        // 각 키워드 Id 및 키워드의 가중치 맵핑
        Map<Long, Integer> keywordWeightMap = keywordIds.stream()
                .collect(Collectors.toMap(
                        keywordId -> keywordId,
                        keywordId -> categoryDAO.selectCategoryById(keywordDAO.selectKeywordById(keywordId).getCategoryId()).getWeight()
                ));

        // 모든 프로젝트 id 리스트 조회
        List<Long> projectIds = projectDAO.getAllProjectIds();
        Map<Long, Integer> projectWeightMap = projectIds.stream()
                .collect(Collectors.toMap(
                        projectId -> projectId,
                        projectId -> {

                            // 프로젝트가 가진 키워드 id를 리스트 조회
                            List<Long> projectKeywordIds = projectKeywordDAO.selectKeywordIdsByProjectId(projectId);

                            // 여기서 keywordWeightMap 을 통해 가중치 합 계산 & 반환
                            return projectKeywordIds.stream()
                                    .filter(keywordWeightMap::containsKey)
                                    .mapToInt(keywordWeightMap::get)
                                    .sum();
                        }
                ));

        // 가중치 합이 높은 순서대로 정렬한 프로젝트 Id 리스트
        List<Long> rankedProjectIds = projectWeightMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(4) // 최대 4개까지
                .toList();


        List<ProjectListDTO> projectDTOList = projectDAO.selectProjectsByIds(rankedProjectIds);

        List<S3ImageVO> allImages = s3ImageDAO.findImagesForPostIds(ImageType.Project, projectIds);
        Map<Long, List<S3ImageVO>> imagesByProjectId = allImages.stream()
                .collect(Collectors.groupingBy(S3ImageVO::getPostId));

        for (ProjectListDTO project : projectDTOList) {
            project.setImages(imagesByProjectId.getOrDefault(project.getProjectId(), emptyList()));
        }

        return projectDTOList;
    }

}
