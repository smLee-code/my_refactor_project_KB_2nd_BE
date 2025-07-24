package org.funding.financialProduct.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ChallengeVO {
    private Long challengeId; // 챌린지 id
    private Long productId; // 상품 ID
    private Integer challengePeriodDays; // 챌린지 기간 (일 단위)
    private String reward; // 리워드
    private String rewardCondition; // 리워드 조건
    private String verifyStandard; // 첼린지 검증 기준
}
