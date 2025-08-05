package org.funding.fund.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.funding.fund.vo.enumType.ProgressType;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FundVO {
    private Long fundId; // 펀딩 id
    private Long productId; // 금융 상품 id
    private Long projectId; // 프로젝트 id
    private ProgressType progress; // 현재 상태
    private LocalDateTime launchAt; // 출시 날짜
    private LocalDateTime endAt; // 종료 날짜
    private String financialInstitution; // 금융사
    private Integer retryVotesCount; // 재출시 투표 갯수
}
