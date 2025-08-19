package org.funding.userLoan.vo;

import lombok.Data;
import org.funding.userLoan.vo.enumType.SuccessType;

import java.time.LocalDateTime;

@Data
public class UserLoanVO {
    private Long userLoanId;
    private Long fundId;
    private Long userId;
    private Integer loanAmount; // 대출 금액
    private SuccessType loanAccess; // 대출 승인 여부
    private boolean fullPayment; // 완납 여부
    private LocalDateTime createAt; // 생성 시간
}
