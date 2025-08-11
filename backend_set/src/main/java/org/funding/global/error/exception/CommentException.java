package org.funding.global.error.exception;

import org.funding.global.error.ErrorCode;

public class CommentException extends BusinessException {
    public CommentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
