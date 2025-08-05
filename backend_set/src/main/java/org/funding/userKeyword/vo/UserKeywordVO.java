package org.funding.userKeyword.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserKeywordVO {

    private Long userKeywordId;
    private Long userId;
    private Long keywordId;
}
