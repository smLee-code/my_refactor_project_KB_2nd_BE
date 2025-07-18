package org.funding.fund.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.funding.financialProduct.dto.FinancialProductDTO;
import org.funding.fund.vo.enumType.ProgressType;

import java.time.LocalDateTime;

public class FundCreateDTO {

    // 펀딩 생성 전 응답받는 프로젝트 정보DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProjectInfoResponseDTO {
        private Long projectId;
        private String title;
        private String promotion;
        private LocalDateTime deadline;
        private LocalDateTime createAt;
        private Long userId;
        
        // 연관된 금융상품 정보
        private FinancialProductDTO baseProduct;
        
        // 상세 금융상품 정보 (타입에 따라 다름)
        private Object productDetail;
    }

    // 펀딩 생성 전 입력받는 정보 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FundCreateRequestDTO {
        private Long projectId;
        private String productType;
        private String financialInstitution;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime launchAt;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime endAt;
        
        // 수정된 금융상품 정보
        private Object modifiedProductDetail;
    }

    // 펀딩 생성 완료 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FundCreatedResponseDTO {
        private Long fundId;
        private Long projectId;
        private Long productId;
        private ProgressType progress;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime launchAt;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime endAt;
        
        private String financialInstitution;
        
        // 프로젝트 정보
        private String projectTitle;
        private String projectPromotion;
        
        // 생성된 금융상품 정보
        private Object finalProductDetail;
    }
}