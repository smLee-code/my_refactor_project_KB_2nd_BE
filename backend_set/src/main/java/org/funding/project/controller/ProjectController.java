package org.funding.project.controller;

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

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

//    //인기프로젝트 조회
//    @GetMapping("/top")
//    public ResponseEntity<List<TopProjectDTO>> getTopProjects() {
//        List<TopProjectDTO> topProjects = projectService.getTopProjects();
//
//        return new ResponseEntity<>(topProjects, HttpStatus.OK);
//    }

    @Auth
    @GetMapping("/top")
    public ResponseEntity<List<TopProjectDTO>> getTopProject(HttpServletRequest request) {
        List<TopProjectDTO> list = projectService.getTopProjects();

        return ResponseEntity.ok(list);
    }

    @Auth
    @GetMapping("/list/detail/{id}")
    public ResponseEntity<ProjectVO> getProjectDetail(@PathVariable("id") Long id,
                                                      HttpServletRequest request) {
        ProjectVO project = projectService.selectProjectById(id);
        return ResponseEntity.ok(project);
    }

    /**
     * 새로 추가: [GET] /api/projects/list/detail/{id}/full
     * 프로젝트 + 타입별 상세 정보까지 조회
     */
    @Auth
    @GetMapping("/list/detail/{id}/full")
    public ResponseEntity<ProjectResponseDTO> getProjectFullDetail(@PathVariable("id") Long id,
                                                                   HttpServletRequest request) {
        ProjectResponseDTO projectDetails = projectService.getProjectDetails(id);
        return ResponseEntity.ok(projectDetails);
    }


    @Auth
    @GetMapping("/distribution/type")
    public List<Map<String, Object>> getProjectTypeDistribution(HttpServletRequest request) {
        return projectService.getProjectTypeDistribution();
    }

    @Auth
    @GetMapping("trend")
    public Map<String, List<Integer>> getProjectTrend(HttpServletRequest request) {
        return projectService.getProjectTrends();
    }


    @Auth
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<ProjectListDTO>> getProjects(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            HttpServletRequest request) {

        List<ProjectListDTO> projectWithDetailList = projectService.getProjectWithDetailList(keyword, type);

        return ResponseEntity.ok(projectWithDetailList);

//        if (keyword != null && !keyword.isEmpty()) {
//            return projectService.searchByKeyword(keyword);
//        } else if (type != null && !type.isEmpty()) {
//            return projectService.searchByType(type);
//        } else {
//            return projectService.getAllProjects();
//        }
    }

    @Auth
    @GetMapping("/related/{id}")
    public ResponseEntity<List<ProjectListDTO>> getRelatedProjects(@PathVariable("id") Long projectId,
                                                                   HttpServletRequest request) {
        List<ProjectListDTO> projectList = projectService.getRelatedProjects(projectId);

        return ResponseEntity.ok(projectList);

    }

    @Auth
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProjectResponseDTO> createProject(
            @RequestPart("projectInfo") CreateProjectRequestDTO createRequestDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        ProjectResponseDTO responseDTO = projectService.createProject(createRequestDTO, images, userId);
        return ResponseEntity.ok(responseDTO);
    }

    @Auth
    @DeleteMapping("/delete/{id}")
    public void deleteProject(@PathVariable("id") Long id,
                              HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        projectService.deleteProject(id, userId);
    }

    /* 키워드 관련 api */
    @Auth
    @GetMapping("/keyword/{id}")
    public ResponseEntity<List<KeywordVO>> getProjectKeywords(
            @PathVariable("id") Long projectId,
            HttpServletRequest request
    ) {
        List<KeywordVO> list = projectService.getProjectKeywords(projectId);

        return ResponseEntity.ok(list);
    }

    @Auth
    @PostMapping("/keyword")
    public ResponseEntity<String> addKeywordIntoProject(
            @RequestBody ProjectKeywordRequestDTO requestDTO,
            HttpServletRequest request
    ) {
        projectService.addKeywordIntoProject(requestDTO);

        return ResponseEntity.ok("키워드 추가 완료");
    }

    @DeleteMapping("/keyword")
    public ResponseEntity<String> deleteKeywordFromProject(
            @RequestBody ProjectKeywordRequestDTO requestDTO,
            HttpServletRequest request
    ) {
        projectService.deleteKeywordFromProject(requestDTO);

        return ResponseEntity.ok("키워드 삭제 완료");
    }
}
