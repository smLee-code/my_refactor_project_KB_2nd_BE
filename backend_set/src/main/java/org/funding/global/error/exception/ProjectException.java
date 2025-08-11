package org.funding.global.error.exception;

import org.funding.global.error.ErrorCode;

public class ProjectException extends BusinessException {
    public ProjectException(ErrorCode errorCode) {
        super(errorCode);
    }
}
