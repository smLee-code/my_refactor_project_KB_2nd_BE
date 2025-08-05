package org.funding.project.vo.enumType;

public enum ProjectType {
    Savings, Loan, Donation, Challenge;

    public static ProjectType fromString(String value) {
        for (ProjectType p : values()) {
            if (p.name().equals(value)) {
                return p;
            }
        }

        throw new IllegalArgumentException("알수없는 피드 타입:" + value);
    }
}
