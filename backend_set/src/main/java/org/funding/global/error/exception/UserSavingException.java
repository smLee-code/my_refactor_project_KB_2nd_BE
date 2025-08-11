package org.funding.global.error.exception;

import org.funding.global.error.ErrorCode;

public class UserSavingException extends BusinessException{
    public UserSavingException(ErrorCode errorCode) {
        super(errorCode);
    }
}
