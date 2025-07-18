package org.funding.project.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DonationProjectDTO extends ProjectDTO {
    private String recipient;
    private String usagePlan;
    private Long targetAmount;
}
