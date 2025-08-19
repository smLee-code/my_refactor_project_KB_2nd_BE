package org.funding.userChallenge.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChallengeParticipantDTO {
    private Long userChallengeId;
    private Long userId;
    private String username;
    private String nickname;
    private int currentCount;
    private int failCount;
}
