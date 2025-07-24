package org.funding.project.controller;

import lombok.RequiredArgsConstructor;
import org.funding.project.dto.response.ProjectListDTO;
import org.funding.project.dto.request.CreateProjectRequestDTO;
import org.funding.project.dto.response.ProjectResponseDTO;
import org.funding.project.service.ProjectService;
import org.funding.project.vo.ProjectVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;


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

    @PostMapping("")
    public ResponseEntity<ProjectResponseDTO> createProject(@RequestBody CreateProjectRequestDTO createRequestDTO) {
        ProjectResponseDTO responseDTO = projectService.createProject(createRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteProject(@PathVariable("id") Long id) {
        projectService.deleteProject(id);
    }

}