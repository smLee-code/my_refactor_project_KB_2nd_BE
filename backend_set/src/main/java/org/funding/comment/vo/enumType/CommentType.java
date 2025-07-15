package org.funding.comment.vo.enumType;

public enum CommentType {
    Funding, Project;

    public static CommentType fromString(String value) {
        for (CommentType commentType : CommentType.values()) {
            if (commentType.name().equals(value)) {
                return commentType;
            }
        }

        throw new IllegalArgumentException("알수없는 댓글 타입:" + value);
    }
}
