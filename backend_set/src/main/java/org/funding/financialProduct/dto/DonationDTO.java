package org.funding.financialProduct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class DonationDTO extends FinancialProductDTO {
    private String recipient;
    private String usagePlan;
    private Integer minDonationAmount;
    private Integer maxDonationAmount;
    private Long targetAmount;
}
