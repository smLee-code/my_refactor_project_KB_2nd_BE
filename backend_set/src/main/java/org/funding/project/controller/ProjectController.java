package org.funding.project.controller;

import lombok.RequiredArgsConstructor;
import org.funding.project.dto.request.CreateProjectRequestDTO;
import org.funding.project.dto.response.ProjectResponseDTO;
import org.funding.project.service.ProjectService;
import org.funding.project.vo.ProjectVO;
import org.springframework.http.ResponseEntity;
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

//    @GetMapping("/list/detail/{id}")
//    public ResponseEntity<ProjectResponseDTO> getProjectDetail(@PathVariable("id") Long id) {
//        ProjectVO project = projectService.selectProjectById(id);
//        return ResponseEntity.ok(project);
//    }

    @PostMapping("")
    public ResponseEntity<ProjectResponseDTO> createProject(@RequestBody CreateProjectRequestDTO createRequestDTO) {
        ProjectResponseDTO responseDTO = projectService.createProject(createRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

}