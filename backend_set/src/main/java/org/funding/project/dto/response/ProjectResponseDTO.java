package org.funding.project.dto.response;

import lombok.Data;
import org.funding.S3.vo.S3ImageVO;
import org.funding.project.vo.ProjectVO;
import org.funding.project.vo.enumType.ProjectProgress;
import org.funding.project.vo.enumType.ProjectType;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectResponseDTO {

    private ProjectVO basicInfo;
    private Object detailInfo;
    private Long voteCount;
    private List<S3ImageVO> imageList;

}