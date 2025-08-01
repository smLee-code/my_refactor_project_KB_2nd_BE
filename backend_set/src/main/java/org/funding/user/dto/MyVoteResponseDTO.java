package org.funding.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyVoteResponseDTO { // 내 투표 조회
    private Long voteId;
    private Long projectId;
    private String projectTitle;
    private LocalDateTime voteTime;
} 