package org.funding.userChallenge.vo.enumType;

public enum ChallengeStatus {

    ONGOING, Success, Fail;

    public static ChallengeStatus fromString(String value) {
        for (ChallengeStatus status : ChallengeStatus.values()) {
            if (status.name().equals(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException("알수없는 권한: " + value);
    }
}
