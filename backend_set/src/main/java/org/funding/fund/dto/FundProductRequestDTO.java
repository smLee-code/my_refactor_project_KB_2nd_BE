package org.funding.fund.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.funding.fund.vo.enumType.FundType;

public class FundProductRequestDTO {

    // 적금 요청 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SavingsRequest {
        // 공통 필드
        private String name;
        private String detail;
        private String thumbnail;
        private String joinCondition;
        
        // 적금 전용 필드
        private Double interestRate;
        private Integer periodDays;
        private String successCondition;
    }

    // 대출 요청 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoanRequest {
        // 공통 필드
        private String name;
        private String detail;
        private String thumbnail;
        private String joinCondition;
        
        // 대출 전용 필드
        private Long loanLimit;
        private Double minInterestRate;
        private Double maxInterestRate;
        private String reward;
        private String rewardCondition;
    }

    // 챌린지 요청 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChallengeRequest {
        // 공통 필드
        private String name;
        private String detail;
        private String thumbnail;
        private String joinCondition;
        
        // 챌린지 전용 필드
        private Integer challengePeriodDays;
        private String reward;
        private String rewardCondition;
    }

    // 기부 요청 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DonationRequest {
        // 공통 필드
        private String name;
        private String detail;
        private String thumbnail;
        private String joinCondition;
        
        // 기부 전용 필드
        private String recipient;
        private String usagePlan;
        private Integer minDonationAmount;
        private Integer maxDonationAmount;
        private Long targetAmount;
    }
}