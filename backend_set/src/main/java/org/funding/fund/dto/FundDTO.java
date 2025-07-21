package org.funding.fund.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.funding.fund.vo.enumType.ProgressType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundDTO {
    private Long fundId;
    private Long productId;
    private Long projectId;
    private ProgressType progress;
    private LocalDateTime launchAt;
    private LocalDateTime endAt;
    private String financialInstitution;
    
    // 연관된 프로젝트 정보
    private String projectTitle;
    private String projectPromotion;
    
    // 연관된 금융상품 정보
    private String productName;
    private Object productDetail;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FundCreateRequestDTO {
        private Long projectId;
        private String productType;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FundCreateFormDTO {
        private Long projectId;
        private String projectTitle;
        private String projectPromotion;
        private Object productDetail;
        private LocalDateTime launchAt;
        private LocalDateTime endAt;
        private String financialInstitution;
    }
}
