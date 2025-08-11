package org.funding.global.error.exception;

import org.funding.global.error.ErrorCode;

public class FundException extends BusinessException{
    public FundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
