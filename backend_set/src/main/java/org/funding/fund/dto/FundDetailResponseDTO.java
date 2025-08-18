package org.funding.fund.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.funding.S3.vo.S3ImageVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.funding.fund.vo.enumType.FundType;
import org.funding.fund.vo.enumType.ProgressType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.funding.keyword.vo.KeywordVO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundDetailResponseDTO {
    
    // Fund 테이블 정보
    private Long fundId;
    private Long productId;
    private Long projectId;
    private ProgressType progress;
    private LocalDateTime launchAt;
    private LocalDateTime endAt;
    private String financialInstitution;
    private int retryVotesCount;
    private List<S3ImageVO> imageUrls;
    
    // Financial Product 테이블 정보
    private String name;
    private String detail;
    private FundType fundType;
    private String iconUrl;
    private String finalUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private String productCondition;
    
    // Savings 테이블 정보 (fundType이 SAVINGS일 때만)
    private Integer periodDays;
    private BigDecimal interestRate;
    private String successCondition;
    
    // Donation 테이블 정보 (fundType이 DONATION일 때만)
    private String recipient;
    private String usagePlan;
    private Long minDonationAmount;
    private Long maxDonationAmount;
    private Long targetAmount;
    
    // Loan 테이블 정보 (fundType이 LOAN일 때만)
    private Long loanLimit;
    private LocalDate repaymentStartDate;
    private LocalDate repaymentEndDate;
    private BigDecimal minInterestRate;
    private BigDecimal maxInterestRate;
    private String loanReward;
    private String loanRewardCondition;
    
    // Challenge 테이블 정보 (fundType이 CHALLENGE일 때만)
    private Integer challengePeriodDays;
    private String challengeReward;
    private String challengeRewardCondition;
    private String verifyStandard;
    
    // Keywords 정보
    @Setter
    private List<KeywordVO> keywords;

    //참여 여부 정보
    private boolean isJoined;
    
    //가입자 수 정보
    private int participantCount;
    
    //현재까지 모인 금액 (챌린지, 기부 펀딩용)
    private Long currentAmount;
}