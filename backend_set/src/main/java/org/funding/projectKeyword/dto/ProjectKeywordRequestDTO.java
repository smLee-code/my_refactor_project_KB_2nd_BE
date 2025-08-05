package org.funding.projectKeyword.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectKeywordRequestDTO {

    private Long projectId;
    private Long keywordId;

}

