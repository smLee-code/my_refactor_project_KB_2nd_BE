package org.funding.global.error.exception;

import org.funding.global.error.ErrorCode;

public class OpenAiException extends BusinessException {
    public OpenAiException(ErrorCode errorCode) {
        super(errorCode);
    }
}
