package org.funding.project.vo.enumType;

public enum ProjectType {
    //Savings: 저축형, Loan: 대출형, Donation: 기부형, Chanllenge: 챌린지
    Savings, Loan, Donation, Challenge;

    public static ProjectType fromString(String value) {
        for (ProjectType f : ProjectType.values()) {
            if (f.name().equals(value)) {
                return f;
            }
        }

        throw new IllegalArgumentException("알수없는 피드 타입:" + value);
    }
}
