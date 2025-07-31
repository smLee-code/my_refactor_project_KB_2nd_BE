package org.funding.project.dto.response;

import lombok.Data;
import org.funding.fund.vo.enumType.ProgressType;
import org.funding.project.vo.enumType.ProjectProgress;
import org.funding.project.vo.enumType.ProjectType;

import java.time.LocalDateTime;

@Data
public class ProjectListDTO {
    private Long projectId;
    private String title;
    private LocalDateTime deadline; // 마감일
    private LocalDateTime createAt; // 생성일 (프로젝트는 수정 못함)
    private String userId;
    private ProjectType projectType;
    private ProjectProgress progress;
    private String promotion;
}
