package org.funding.project.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

import org.funding.keyword.dto.KeywordRequestDTO;
import org.funding.keyword.dto.KeywordResponseDTO;

import org.funding.keyword.vo.KeywordVO;
import org.funding.project.dto.response.ProjectListDTO;
import org.funding.project.dto.request.CreateProjectRequestDTO;
import org.funding.project.dto.response.ProjectResponseDTO;
import org.funding.project.dto.response.TopProjectDTO;
import org.funding.project.service.ProjectService;
import org.funding.project.vo.ProjectVO;

import org.funding.projectKeyword.dto.ProjectKeywordRequestDTO;

import org.funding.projectKeyword.service.ProjectKeywordService;
import org.funding.security.util.Auth;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
@Api(tags = "프로젝트 API")
@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @ApiOperation(value = "인기 프로젝트 목록 조회", notes = "투표(좋아요) 수가 가장 많은 상위 프로젝트 목록을 조회합니다.")
    @GetMapping("/top")
    public ResponseEntity<List<TopProjectDTO>> getTopProject() {
        List<TopProjectDTO> list = projectService.getTopProjects();
        return ResponseEntity.ok(list);
    }

    @ApiOperation(value = "프로젝트 기본 정보 조회", notes = "특정 프로젝트의 기본 정보를 조회합니다.")
    @GetMapping("/list/detail/{id}")
    public ResponseEntity<ProjectVO> getProjectDetail(
            @ApiParam(value = "조회할 프로젝트 ID", required = true, example = "1") @PathVariable("id") Long id) {
        ProjectVO project = projectService.selectProjectById(id);
        return ResponseEntity.ok(project);
    }

    @ApiOperation(value = "사용자 맞춤 프로젝트 추천", notes = "로그인한 사용자의 관심 키워드를 기반으로 관련된 프로젝트 목록을 추천합니다.")
    @Auth
    @GetMapping("/list/keyword")
    public ResponseEntity<List<ProjectListDTO>> getRecommendedProjects(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<ProjectListDTO> list = projectService.recommendProjectsByUserKeywords(userId);
        return ResponseEntity.ok(list);
    }

    @ApiOperation(value = "프로젝트 상세 정보 조회", notes = "특정 프로젝트의 모든 상세 정보(타입별 정보 포함)를 조회합니다.")
    @GetMapping("/list/detail/{id}/full")
    public ResponseEntity<ProjectResponseDTO> getProjectFullDetail(
            @ApiParam(value = "조회할 프로젝트 ID", required = true, example = "1") @PathVariable("id") Long id) {
        ProjectResponseDTO projectDetails = projectService.getProjectDetails(id);
        return ResponseEntity.ok(projectDetails);
    }

    @ApiOperation(value = "프로젝트 유형별 분포 조회 (대시보드용)", notes = "전체 프로젝트의 타입(저축, 대출 등)별 분포 데이터를 조회합니다.")
    @GetMapping("/distribution/type")
    public List<Map<String, Object>> getProjectTypeDistribution() {
        return projectService.getProjectTypeDistribution();
    }

    @ApiOperation(value = "프로젝트 등록 추이 조회 (대시보드용)", notes = "최근 프로젝트 등록 추이 데이터를 조회합니다.")
    @GetMapping("trend")
    public Map<String, List<Integer>> getProjectTrend() {
        return projectService.getProjectTrends();
    }

    @ApiOperation(value = "프로젝트 목록 필터링 조회", notes = "키워드와 프로젝트 타입을 기준으로 프로젝트 목록을 필터링하여 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<ProjectListDTO>> getProjects(
            @ApiParam(value = "검색할 키워드 (선택)") @RequestParam(required = false) String keyword,
            @ApiParam(value = "필터링할 프로젝트 타입 (선택)") @RequestParam(required = false) String type,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        List<ProjectListDTO> projectWithDetailList = projectService.getProjectWithDetailList(keyword, type, userId);
        return ResponseEntity.ok(projectWithDetailList);
    }

    @ApiOperation(value = "연관 프로젝트 목록 조회", notes = "특정 프로젝트와 관련된 다른 프로젝트 목록을 조회합니다.")
    @GetMapping("/related/{id}")
    public ResponseEntity<List<ProjectListDTO>> getRelatedProjects(
            @ApiParam(value = "기준 프로젝트 ID", required = true, example = "1") @PathVariable("id") Long projectId) {
        List<ProjectListDTO> projectList = projectService.getRelatedProjects(projectId);
        return ResponseEntity.ok(projectList);
    }

    @ApiOperation(value = "프로젝트 생성", notes = "새로운 프로젝트를 생성합니다. 프로젝트 정보(projectInfo)와 이미지 파일(images)을 multipart/form-data 형식으로 전송해야 합니다.")
    @Auth
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProjectResponseDTO> createProject(
            @RequestPart("projectInfo") CreateProjectRequestDTO createRequestDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest request
    ) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        ProjectResponseDTO responseDTO = projectService.createProject(createRequestDTO, images, userId);
        return ResponseEntity.ok(responseDTO);
    }

    @ApiOperation(value = "프로젝트 삭제 (생성자/관리자용)", notes = "특정 프로젝트를 삭제합니다. 프로젝트 생성자 또는 관리자만 삭제할 수 있습니다.")
    @Auth
    @DeleteMapping("/delete/{id}")
    public void deleteProject(
            @ApiParam(value = "삭제할 프로젝트 ID", required = true, example = "1") @PathVariable("id") Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        projectService.deleteProject(id, userId);
    }

    // --- 키워드 관련 API ---

    @ApiOperation(value = "프로젝트의 키워드 목록 조회", notes = "특정 프로젝트에 등록된 모든 키워드를 조회합니다.")
    @Auth
    @GetMapping("/keyword/{id}")
    public ResponseEntity<List<KeywordVO>> getProjectKeywords(
            @ApiParam(value = "키워드를 조회할 프로젝트 ID", required = true, example = "1") @PathVariable("id") Long projectId,
            HttpServletRequest request
    ) {
        List<KeywordVO> list = projectService.getProjectKeywords(projectId);
        return ResponseEntity.ok(list);
    }

    @ApiOperation(value = "프로젝트에 키워드 추가", notes = "기존 프로젝트에 새로운 키워드를 연결합니다.")
    @Auth
    @PostMapping("/keyword")
    public ResponseEntity<String> addKeywordIntoProject(
            @RequestBody ProjectKeywordRequestDTO requestDTO,
            HttpServletRequest request
    ) {
        projectService.addKeywordIntoProject(requestDTO);
        return ResponseEntity.ok("키워드 추가 완료");
    }

    @ApiOperation(value = "프로젝트의 키워드 삭제", notes = "기존 프로젝트와 연결된 키워드를 삭제합니다.")
    @DeleteMapping("/keyword")
    public ResponseEntity<String> deleteKeywordFromProject(
            @RequestBody ProjectKeywordRequestDTO requestDTO,
            HttpServletRequest request
    ) {
        projectService.deleteKeywordFromProject(requestDTO);
        return ResponseEntity.ok("키워드 삭제 완료");
    }
}