package org.funding.project.vo.enumType;

public enum ProjectProgress {
    Active, Closed, FUNDED;

    public static ProjectProgress fromString(String value) {
        for (ProjectProgress p : values()) {
            if (p.name().equals(value)) {
                return p;
            }
        }

        throw new IllegalArgumentException("알수없는 피드 타입:" + value);
    }
}
