package org.funding.interestingKeyword.vo;

import lombok.Data;

@Data
public class InterestingKeywordVO {
    private Long interestId; // 키워드 id
    private String keyword; // 키워드

    private Long userId; // 해당 키워드를 가진 유저 id(1대다)
}
