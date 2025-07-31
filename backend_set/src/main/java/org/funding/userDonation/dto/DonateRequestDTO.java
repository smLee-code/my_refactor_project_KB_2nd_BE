package org.funding.userDonation.dto;

import lombok.Data;

@Data
public class DonateRequestDTO {
    private Long fundId;
    private Long userId;
    private Integer donateAmount;
    private boolean anonymous; // 익명 여부
}
