package org.funding.userLoan.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.funding.userLoan.vo.enumType.SuccessType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoanApplicationDTO {
    private Long userLoanId;
    private Long userId;
    private String username;
    private String nickname;
    private Integer loanAmount;
    private SuccessType status;
}