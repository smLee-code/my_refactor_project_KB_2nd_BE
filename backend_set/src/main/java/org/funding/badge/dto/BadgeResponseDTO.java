package org.funding.badge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BadgeResponseDTO {
    private Long badgeId;
    private String name;
    private String description;
    private String autoGrantCondition;
}
