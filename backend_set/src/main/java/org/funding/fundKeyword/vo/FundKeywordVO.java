package org.funding.fundKeyword.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundKeywordVO {
    private Long fundKeywordId;
    private Long fundId;
    private Long keywordId;
}