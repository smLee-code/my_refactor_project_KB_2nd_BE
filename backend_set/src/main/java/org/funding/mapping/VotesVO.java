package org.funding.mapping;

import lombok.Data;

@Data
public class VotesVO {
    private Long voteId; // 투표 id
    private Long userId; // 투표한 사용자 id
    private Long projectId; // 투표된 프로젝트 id
}
