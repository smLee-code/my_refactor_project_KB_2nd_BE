package org.funding.global.error;

import lombok.Getter;
@Getter
public class ErrorResponse {

    private final int status;       // HTTP 상태 코드
    private final String code;         // 커스텀 에러 코드
    private final String message;      // 에러 메시지

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }
}