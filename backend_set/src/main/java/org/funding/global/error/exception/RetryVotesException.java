package org.funding.global.error.exception;

import org.funding.global.error.ErrorCode;

public class RetryVotesException extends BusinessException {
    public RetryVotesException(ErrorCode errorCode) {
        super(errorCode);
    }
}
