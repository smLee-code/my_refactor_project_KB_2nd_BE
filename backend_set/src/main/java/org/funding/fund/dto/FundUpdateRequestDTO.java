package org.funding.fund.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.funding.fund.vo.enumType.FundType;
import org.funding.fund.vo.enumType.ProgressType;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundUpdateRequestDTO {
    
    // Fund 정보 수정
    private ProgressType progress;
    private LocalDateTime launchAt;
    private LocalDateTime endAt;
    private String financialInstitution;
    
    // Financial Product 정보 수정
    private String name;
    private String detail;
    private String iconUrl;
    private String productCondition;
    
    // 상품 타입
    private FundType fundType;
    
    // 타입별 상세 정보를 담는 Object
    // 각 상품 타입별로 다른 구조의 JSON 객체를 받을 수 있음
    private Object productDetails;
}