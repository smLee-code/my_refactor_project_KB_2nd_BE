package org.funding.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.funding.project.vo.enumType.ProjectProgress;
import org.funding.project.vo.enumType.ProjectType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyProjectResponseDTO { // 작성한 프로젝트 조회
    private Long projectId;
    private String title;
    private String promotion;
    private ProjectType projectType;
    private ProjectProgress progress;
    private LocalDateTime deadline;
    private LocalDateTime createAt;
} 