package org.funding.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordResponseDTO { // 관심 키워드 조회
    private Long interestId;
    private String keyword;
} 