package org.funding.keyword.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeywordVO {
    private Long keywordId;
    private Long categoryId;
    private String name;
}
