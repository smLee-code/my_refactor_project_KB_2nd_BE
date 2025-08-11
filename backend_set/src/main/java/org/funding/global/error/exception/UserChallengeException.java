package org.funding.global.error.exception;

import org.funding.global.error.ErrorCode;

public class UserChallengeException extends BusinessException{
    public UserChallengeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
