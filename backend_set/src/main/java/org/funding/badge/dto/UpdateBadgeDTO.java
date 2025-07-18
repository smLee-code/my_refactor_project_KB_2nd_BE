package org.funding.badge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBadgeDTO {
    private String name; // 이름
    private String description; // 설명
    private String autoGrantCondition; // 자동 부여 조건
}
