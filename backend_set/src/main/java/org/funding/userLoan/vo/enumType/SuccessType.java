package org.funding.userLoan.vo.enumType;

import org.funding.userChallenge.vo.enumType.ChallengeStatus;

public enum SuccessType {
    APPROVED, REJECTED, PENDING, DONE;
    // 허가, 비허가, 대기중, 지급완료

    public static SuccessType fromString(String value) {
        for (SuccessType status : SuccessType.values()) {
            if (status.name().equals(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException("알수 없는 상태: " + value);
    }
}
