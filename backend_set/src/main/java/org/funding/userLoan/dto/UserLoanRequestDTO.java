package org.funding.userLoan.dto;

import lombok.Data;

@Data
public class UserLoanRequestDTO {
    private Long userId;
    private Integer loanAmount; // 대출금
}
