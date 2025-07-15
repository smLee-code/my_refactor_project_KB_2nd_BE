package org.funding.fund.vo;

import org.funding.fund.vo.enumType.FundType;

import java.math.BigDecimal;

public class FinancialProductVO {
    private Long productId; // 상품 id
    private String name; // 상품 이름
    private String detail; // 상품 내용
    private FundType fundType; // ENUM: "예금", "적금", "챌린지", "기부"
    private BigDecimal targetAmount; // 목표 금액
    private BigDecimal expectedProfit; // 예상 금액
    private Long originalProductId; // 수정 전 상품 (펀딩 출시로 상품 내용이 변경되는 경우, 원래의 상품에 대한 정보)

    // 하위 타입 포함 (선택)
    private DepositVO deposit;
    private InstallmentVO installment;
    private ChallengeVO challenge;
    private DonationVO donation;
}
