package org.funding.comment.vo;

import lombok.Data;
import org.funding.comment.vo.enumType.CommentType;

import java.time.LocalDateTime;

@Data
public class CommentVO {
    private Long commentId; // 댓글 id
    private String content; // 댓글 내용
    private LocalDateTime createAt; // 댓글 생성일

    private CommentType commentType; // 댓글 타입
    private Long postId; // 해당 엔티티 id
}
