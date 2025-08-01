package org.funding.project.dto.response;

import lombok.Data;
import org.funding.project.vo.enumType.ProjectProgress;
import org.funding.project.vo.enumType.ProjectType;

import java.time.LocalDateTime;

@Data
public class TopProjectDTO {
    private Long projectId;
    private ProjectType projectType;
    private String title;
    private String promotion;
    private ProjectProgress progress;
    private LocalDateTime deadline;
    private LocalDateTime createAt;
    private Integer voteCount;
}
