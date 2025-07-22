package org.funding.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicateVoteException extends RuntimeException {

    public DuplicateVoteException() {
        super("이미 투표한 프로젝트입니다. (중복 투표 불가)");
    }

    public DuplicateVoteException(String message) {
        super(message);
    }

    public DuplicateVoteException(String message, Throwable cause) {
        super(message, cause);
    }
}
