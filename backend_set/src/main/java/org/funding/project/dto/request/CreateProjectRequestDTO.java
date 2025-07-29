package org.funding.project.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.funding.project.vo.ProjectVO;
import org.funding.project.vo.enumType.ProjectProgress;
import org.funding.project.vo.enumType.ProjectType;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * api 요청 (create)을 보낼 때 사용하는 dto
 */

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "projectType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateSavingsProjectRequestDTO.class, name = "Savings"),
        @JsonSubTypes.Type(value = CreateLoanProjectRequestDTO.class, name = "Loan"),
        @JsonSubTypes.Type(value = CreateDonationProjectRequestDTO.class, name = "Donation"),
        @JsonSubTypes.Type(value = CreateChallengeProjectRequestDTO.class, name = "Challenge")
})
public abstract class CreateProjectRequestDTO {

    // Project 공통 칼럼
    private Long userId; // 제안자 id
    private ProjectType projectType; // 프로젝트 타입
    private String title; // 프로젝트 제목
    private String promotion; // 프로젝트 홍보글
//    private ProjectProgress progress; // 프로젝트 진행도
    private LocalDate deadline; // 마감일

    public ProjectVO toCommonVO() {
        return ProjectVO.builder()
                .userId(this.getUserId())
                .projectType(this.getProjectType())
                .title(this.getTitle())
                .promotion(this.getPromotion())
                .progress(ProjectProgress.Active)
                .deadline(this.getDeadline().atStartOfDay())
                .build();
    }
}