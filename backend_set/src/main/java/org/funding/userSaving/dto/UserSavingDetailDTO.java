package org.funding.userSaving.dto;

import lombok.Data;

@Data
public class UserSavingDetailDTO {

    private Long userSavingId;
    private Integer savingAmount;

    private String savingName;
    private Integer periodDays;
    private Double interestRate;
    private String savingImageUrl;
}
