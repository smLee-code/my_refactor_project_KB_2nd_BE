package org.funding.global.error.exception;

import org.funding.global.error.ErrorCode;

public class PaymentException extends BusinessException{
    public PaymentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
