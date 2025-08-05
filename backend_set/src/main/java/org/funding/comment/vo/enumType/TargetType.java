package org.funding.comment.vo.enumType;

public enum TargetType {
    Funding, Project;

    public static TargetType fromString(String value) {
        for (TargetType commentType : TargetType.values()) {
            if (commentType.name().equals(value)) {
                return commentType;
            }
        }

        throw new IllegalArgumentException("알수없는 댓글 타입:" + value);
    }
}
