package org.funding.project.vo.enumType;

public enum ProjectProgress {

    Active, Closed;

    public static ProjectProgress fromString(String value) {
        for (ProjectProgress f : ProjectProgress.values()) {
            if (f.name().equals(value)) {
                return f;
            }
        }

        throw new IllegalArgumentException("알수없는 피드 타입:" + value);
    }
}
