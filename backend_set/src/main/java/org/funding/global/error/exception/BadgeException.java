package org.funding.global.error.exception;

import org.funding.global.error.ErrorCode;

public class BadgeException extends BusinessException {
    public BadgeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
