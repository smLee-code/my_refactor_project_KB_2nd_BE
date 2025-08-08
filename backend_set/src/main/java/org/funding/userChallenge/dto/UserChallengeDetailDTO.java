package org.funding.userChallenge.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserChallengeDetailDTO {

    private Long userChallengeId;
    private int currentCount;
    private int failCount;
    private String challengeStatus;


    private String challengeName;
    private String challengeImageUrl;
    private LocalDate challengeStartDate;
    private LocalDate challengeEndDate;
    private String reward;
    private String verifyStandard;

}
