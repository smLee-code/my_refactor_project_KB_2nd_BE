package org.funding.project.dto;

import lombok.Data;
import org.funding.fund.vo.enumType.FundType;
//import org.funding.project.vo.enumType.ProjProgress;

import java.time.LocalDateTime;

@Data
public class ProjectDTO {
    private Long projectId;
    private Long userId;
    private FundType projectType;
//    private ProjProgress progress;
    private String title;
    private String promotion;
    private LocalDateTime deadline;
    private LocalDateTime createAt;
}