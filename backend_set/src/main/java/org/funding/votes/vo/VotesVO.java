package org.funding.votes.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VotesVO {
    private Long voteId; // 투표 id
    private Long userId; // 투표한 사용자 id
    private Long projectId; // 투표된 프로젝트 id
    private LocalDateTime voteTime; // 투표 날짜
}
