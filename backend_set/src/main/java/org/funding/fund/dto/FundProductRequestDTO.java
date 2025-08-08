package org.funding.fund.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.funding.fund.vo.enumType.ProgressType;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public class FundProductRequestDTO {

    // 저축 요청 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SavingsRequest {

        // 공통 필드
        private String name;
        private String detail;
        private String joinCondition;
        
        // 저축 전용 필드
        private Double interestRate;
        private Integer periodDays;
        private String successCondition;
        
        // Fund 생성 필드
        private Long projectId;
        private ProgressType progress;
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate launchDate; // 시작일 (시간은 00:00:00으로 고정)
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate; // 종료일 (시간은 23:59:59로 고정)
        
        private String financialInstitution;
        
        // 키워드 필드
        private List<Long> keywordIds;
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
        
        // Fund 생성 필드
        private Long projectId;
        private ProgressType progress;
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate launchDate; // 시작일 (시간은 00:00:00으로 고정)
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate; // 종료일 (시간은 23:59:59로 고정)
        
        private String financialInstitution;
        
        // 키워드 필드
        private List<Long> keywordIds;
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
        private String verifyStandard; // 첼린지 검증 기준
        private LocalDate challengeStartDate;
        private LocalDate challengeEndDate;
        
        // Fund 생성 필드
        private Long projectId;
        private ProgressType progress;
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate launchDate; // 시작일 (시간은 00:00:00으로 고정)
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate; // 종료일 (시간은 23:59:59로 고정)
        
        private String financialInstitution;
        
        // 키워드 필드
        private List<Long> keywordIds;
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
        
        // Fund 생성 필드
        private Long projectId;
        private ProgressType progress;
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate launchDate; // 시작일 (시간은 00:00:00으로 고정)
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate; // 종료일 (시간은 23:59:59로 고정)
        
        private String financialInstitution;
        
        // 키워드 필드
        private List<Long> keywordIds;
    }
}