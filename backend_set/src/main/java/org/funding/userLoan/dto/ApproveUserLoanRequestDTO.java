package org.funding.userLoan.dto;

import lombok.Data;
import org.funding.userLoan.vo.enumType.SuccessType;

@Data
public class ApproveUserLoanRequestDTO {
    private SuccessType type;
    private Long userLoanId;
}
