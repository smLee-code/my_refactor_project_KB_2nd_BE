package org.funding.openAi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.funding.fund.vo.enumType.FundType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryFundRequestDTO {
    private String fundName; // 펀딩 상품 이름
    private FundType fundType; // 펀딩 타입
    private String content; // 펀딩 상품 내용
}
