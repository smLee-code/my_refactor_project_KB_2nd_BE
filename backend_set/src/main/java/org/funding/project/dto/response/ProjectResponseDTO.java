package org.funding.project.dto.response;

import lombok.Data;
import org.funding.project.vo.ProjectVO;
import org.funding.project.vo.enumType.ProjectProgress;
import org.funding.project.vo.enumType.ProjectType;

import java.time.LocalDateTime;

@Data
public class ProjectResponseDTO {

    private ProjectVO basicInfo;
    private Object detailInfo;
    private Long voteCount;

}