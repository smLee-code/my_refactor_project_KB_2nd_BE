package org.funding.challengeLog.vo;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ChallengeLogVO {
    private Long logId;
    private Long userChallengeId;
    private Long userId;
    private boolean verified;
    private String imageUrl;
    private String verifiedResult;
    private LocalDate logDate;
    private Timestamp createAt;
}
