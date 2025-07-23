package org.funding.retryVotes.vo;

import lombok.Data;

@Data
public class RetryVotesVO {
    private Long retryId;
    private Long userId; // 재 출시 희망자 id
    private Long fundingId; // 펀딩 id
}
