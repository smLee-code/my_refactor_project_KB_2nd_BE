package org.funding.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.funding.fund.vo.enumType.ProgressType;
import org.funding.project.vo.ProjectVO;
import org.funding.project.vo.enumType.ProjectProgress;
import org.funding.project.vo.enumType.ProjectType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectListDTO {
    private Long projectId;
    private String title;
    private LocalDateTime deadline; // 마감일
    private LocalDateTime createAt; // 생성일 (프로젝트는 수정 못함)
    private Long userId;
    private ProjectType projectType;
    private ProjectProgress progress;
    private String promotion;

    public static ProjectListDTO fromVO(ProjectVO vo) {
        return ProjectListDTO.builder()
                .projectId(vo.getProjectId())
                .title(vo.getTitle())
                .deadline(vo.getDeadline())
                .createAt(vo.getCreateAt())
                .userId(vo.getUserId())
                .projectType(vo.getProjectType())
                .progress(vo.getProgress())
                .promotion(vo.getPromotion())
                .build();
    }
}
