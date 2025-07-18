package org.funding.financialProduct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.funding.fund.vo.enumType.FundType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FinancialProductDTO {
    private Long productId;
    private String name;
    private String detail;
    private FundType fundType;
    private String thumbnail;
    private String joinCondition;
}
