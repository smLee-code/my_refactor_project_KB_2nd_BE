package org.funding.challengeLog.vo.enumType;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum VerifyType {
    // ENUM: "검증됨", "검증되지 않음", "휴먼검증필요"
    Verified, UnVerified, HumanVerify;

    public static VerifyType fromString(String value) {
        for (VerifyType verifyType : VerifyType.values()) {
            if (verifyType.name().equals(value)) {
                return verifyType;
            }
        }
        throw new IllegalArgumentException(value);
    }
}