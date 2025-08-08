package org.funding.userDonation.dto;

import lombok.Data;

@Data
public class UserDonationDetailDTO {
    private Long userDonationId;
    private Integer donationAmount;
    private boolean anonymous;

    private String donationName;
    private String recipient;
    private Long targetAmount;
    private String donationImageUrl;
}
