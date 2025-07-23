package org.funding.fund.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.funding.fund.vo.enumType.ProgressType;
import org.funding.fund.vo.enumType.FundType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundListResponseDTO {
    // Fund 정보
    private Long fundId;
    private Long productId;
    private Long projectId;
    private ProgressType progress;
    private LocalDateTime launchAt;
    private LocalDateTime endAt;
    private String financialInstitution;
    private int retryVotesCount;
    
    // FinancialProduct 정보
    private String name;
    private String thumbnail;
    private FundType fundType;
}