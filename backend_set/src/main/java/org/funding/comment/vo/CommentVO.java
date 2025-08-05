package org.funding.comment.vo;

import lombok.Data;
import org.funding.comment.vo.enumType.TargetType;

import java.time.LocalDateTime;

@Data
public class CommentVO {
    private Long commentId; // 댓글 id
    private Long userId; // 유저 id

    private TargetType targetType; // 댓글 타입
    private Long targetId; // 해당 엔티티 id

    private String content; // 댓글 내용
    private LocalDateTime createAt; // 댓글 생성일
}
