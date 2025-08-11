package org.funding.global.error.exception;

import org.funding.global.error.ErrorCode;

public class UserLoanException extends BusinessException {
    public UserLoanException(ErrorCode errorCode) {
        super(errorCode);
    }
}
