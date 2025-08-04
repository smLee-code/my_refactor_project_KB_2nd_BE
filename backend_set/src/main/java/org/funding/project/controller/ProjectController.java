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

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

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

    @GetMapping("/top")
    public ResponseEntity<List<TopProjectDTO>> getTopProject() {
        List<TopProjectDTO> list = projectService.getTopProjects();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/list/detail/{id}")
    public ResponseEntity<ProjectVO> getProjectDetail(@PathVariable("id") Long id) {
        ProjectVO project = projectService.selectProjectById(id);
        return ResponseEntity.ok(project);
    }

    /**
     * 새로 추가: [GET] /api/projects/list/detail/{id}/full
     * 프로젝트 + 타입별 상세 정보까지 조회
     */
    @GetMapping("/list/detail/{id}/full")
    public ResponseEntity<ProjectResponseDTO> getProjectFullDetail(@PathVariable("id") Long id) {
        ProjectResponseDTO projectDetails = projectService.getProjectDetails(id);
        return ResponseEntity.ok(projectDetails);
    }

    @GetMapping("/list")
    @ResponseBody
    public List<ProjectListDTO> getProjects(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type) {

        if (keyword != null && !keyword.isEmpty()) {
            return projectService.searchByKeyword(keyword);
        } else if (type != null && !type.isEmpty()) {
            return projectService.searchByType(type);
        } else {
            return projectService.getAllProjects();
        }
    }

    @GetMapping("/related/{id}")
    public ResponseEntity<List<ProjectListDTO>> getRelatedProjects(@PathVariable("id") Long projectId) {
        List<ProjectListDTO> projectList = projectService.getRelatedProjects(projectId);

        return ResponseEntity.ok(projectList);

    }

    @PostMapping("")
    public ResponseEntity<ProjectResponseDTO> createProject(@RequestBody CreateProjectRequestDTO createRequestDTO) {
        ProjectResponseDTO responseDTO = projectService.createProject(createRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteProject(@PathVariable("id") Long id) {
        projectService.deleteProject(id);
    }

    /* 키워드 관련 api */

    @GetMapping("/keyword/{id}")
    public ResponseEntity<List<KeywordVO>> getProjectKeywords(@PathVariable("id") Long projectId) {
        List<KeywordVO> list = projectService.getProjectKeywords(projectId);

        return ResponseEntity.ok(list);
    }

    @PostMapping("/keyword")
    public ResponseEntity<String> addKeywordIntoProject(@RequestBody ProjectKeywordRequestDTO requestDTO) {

        projectService.addKeywordIntoProject(requestDTO);

        return ResponseEntity.ok("키워드 추가 완료");
    }

    @DeleteMapping("/keyword")
    public ResponseEntity<String> deleteKeywordFromProject(@RequestBody ProjectKeywordRequestDTO requestDTO) {

        projectService.deleteKeywordFromProject(requestDTO);

        return ResponseEntity.ok("키워드 삭제 완료");
    }
}
