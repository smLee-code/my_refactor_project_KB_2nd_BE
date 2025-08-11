package org.funding.global.error.exception;

import org.funding.global.error.ErrorCode;

public class CategoryException extends BusinessException {
    public CategoryException(ErrorCode errorCode) {
        super(errorCode);
    }
}
