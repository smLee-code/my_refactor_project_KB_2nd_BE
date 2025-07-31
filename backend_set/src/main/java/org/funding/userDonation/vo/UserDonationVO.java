package org.funding.userDonation.vo;

import lombok.Data;

@Data
public class UserDonationVO {
    private Long userDonationId;
    private Long userId;
    private Integer donationAmount; // 기부 금액
    private boolean anonymous; // 익명 여부
}
