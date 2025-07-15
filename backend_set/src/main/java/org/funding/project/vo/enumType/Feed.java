package org.funding.project.vo.enumType;

public enum Feed {
    Finance, Donation, Challenge;

    public static Feed fromString(String value) {
        for (Feed f : values()) {
            if (f.name().equals(value)) {
                return f;
            }
        }

        throw new IllegalArgumentException("알수없는 피드 타입:" + value);
    }
}
