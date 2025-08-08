package org.funding.userLoan.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserLoanDetailDTO {
    // 내 대출 정보 (from user_loan)
    private Long userLoanId;
    private Integer loanAmount;
    private boolean fullPayment;
    private String loanAccess; // SuccessType은 String으로 변환하여 전달

    // 대출 상품 정보 (from loan, financial_product)
    private String loanName; // 상품 이름
    private Long loanLimit;
    private Double minInterestRate;
    private Double maxInterestRate;
    private LocalDateTime repaymentStartDate;
    private LocalDateTime repaymentEndDate;
}
