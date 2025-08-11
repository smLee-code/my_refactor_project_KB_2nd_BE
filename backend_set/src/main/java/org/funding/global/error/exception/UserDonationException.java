package org.funding.global.error.exception;

import org.funding.global.error.ErrorCode;

public class UserDonationException extends BusinessException {
    public UserDonationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
