package org.funding.fund.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChallengeVO {
    private Long challengeId; // 첼린지 id
    private Long fundingId; // 펀딩 id
    private String challengeName;
    private String goalDescription; // 목표 글
    private Integer donationDays; // 첼린지 일수
    private BigDecimal rewardAmount; // 보상 금액
}
