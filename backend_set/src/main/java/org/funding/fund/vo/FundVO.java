package org.funding.fund.vo;

import lombok.Data;
import org.funding.fund.vo.enumType.ProgressType;

import java.time.LocalDateTime;

@Data
public class FundVO {
    private Long fundId; // 펀딩 id
    private Long productId; // 금융 상품 id
    private ProgressType progress; // 현재 상태
    private LocalDateTime launchAt; // 출시 날짜
    private LocalDateTime endAt; // 종료 날짜
    private String financialInstitution; // 금융사
}
