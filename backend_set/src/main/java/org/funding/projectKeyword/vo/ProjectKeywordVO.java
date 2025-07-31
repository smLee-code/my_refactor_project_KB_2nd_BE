package org.funding.projectKeyword.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectKeywordVO {
    private Long projectKeywordId;
    private Long projectId;
    private Long keywordId;
}
