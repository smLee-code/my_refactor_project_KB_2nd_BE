package org.funding.fund.vo.enumType;

public enum FundType {
    // ENUM: "예금", "적금", "챌린지", "기부"
    Deposit, Installment, Challenge, Donation;

    public static FundType fromString(String value) {
        for (FundType fundType : FundType.values()) {
            if (fundType.name().equals(value)) {
                return fundType;
            }
        }
        throw new IllegalArgumentException(value);
    }
}
