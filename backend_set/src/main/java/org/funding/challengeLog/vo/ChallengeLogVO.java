package org.funding.challengeLog.vo;

import lombok.Data;
import org.funding.challengeLog.vo.enumType.VerifyType;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ChallengeLogVO {
    private Long logId;
    private Long userChallengeId;
    private Long userId;
    private VerifyType verified;
    private String imageUrl;
    private String verifiedResult;
    private LocalDate logDate;
    private Timestamp createAt;
}
