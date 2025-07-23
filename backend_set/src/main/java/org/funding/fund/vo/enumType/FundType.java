package org.funding.fund.vo.enumType;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum FundType {
    // ENUM: "저축", "대출", "챌린지", "기부"
    Savings, Loan, Challenge, Donation;

    public static FundType fromString(String value) {
        for (FundType fundType : FundType.values()) {
            if (fundType.name().equals(value)) {
                return fundType;
            }
        }
        throw new IllegalArgumentException(value);
    }
}
