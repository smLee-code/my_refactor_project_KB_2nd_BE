package org.funding.fund.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DonationVO {
    private Long donationId; // 기부 상품 id
    private Long fundingId; // 펀딩 id
    private String donationName; // 기부 이름
    private String donationPurpose; // 기부 목적
    private String donationContent; // 기부 글
    private BigDecimal targetAmount; // 목표 금액
    private BigDecimal currentAmount; // 현재 금액
    private Long peopleCount; // 현재 참여자 수
}
