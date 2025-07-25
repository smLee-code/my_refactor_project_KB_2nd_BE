package org.funding.userChallenge.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ChallengeRequestDTO {
    private String imageUrl;
    private LocalDate date;
    private Long userId;
}
