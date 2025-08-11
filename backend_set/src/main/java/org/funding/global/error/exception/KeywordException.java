package org.funding.global.error.exception;

import org.funding.global.error.ErrorCode;

public class KeywordException extends BusinessException {
    public KeywordException(ErrorCode errorCode) {
        super(errorCode);
    }
}
