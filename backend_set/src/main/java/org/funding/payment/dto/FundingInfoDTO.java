package org.funding.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundingInfoDTO {
    private Long fundId;
    private Long productId;
    private String fundType;    // donation, challenge, loan, savings
}