package org.funding.comment.dto;

import lombok.Data;
import org.funding.comment.vo.CommentVO;
import org.funding.comment.vo.enumType.TargetType;

@Data
public class InsertCommentRequestDTO {

    private Long userId;
    private String content;

    private TargetType targetType;
    private Long targetId;


    public CommentVO toVO() {
        CommentVO commentVO = new CommentVO();

        commentVO.setUserId(userId);
        commentVO.setContent(content);
        commentVO.setTargetType(targetType);
        commentVO.setTargetId(targetId);

        return commentVO;
    }
}
