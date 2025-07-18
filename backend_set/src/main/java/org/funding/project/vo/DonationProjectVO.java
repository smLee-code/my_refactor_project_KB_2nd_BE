package org.funding.project.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DonationProjectVO extends ProjectVO {
    private String recipient;
    private String usagePlan;
    private Long targetAmount;
}
