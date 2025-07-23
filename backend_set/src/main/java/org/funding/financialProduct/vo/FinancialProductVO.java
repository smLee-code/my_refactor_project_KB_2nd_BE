package org.funding.financialProduct.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.funding.fund.vo.enumType.FundType;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FinancialProductVO {
    private Long productId; // 상품 id
    private String name; // 상품 이름
    private String detail; // 상품 내용
    private FundType fundType; // ENUM: "저축", "대출", "챌린지", "기부"
    private String thumbnail; //대표 이미지
    private String joinCondition; //가입 조건
}
