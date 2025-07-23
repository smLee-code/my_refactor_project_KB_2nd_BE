package org.funding.comment.dto;

import lombok.Data;
import org.funding.comment.vo.CommentVO;
import org.funding.comment.vo.enumType.TargetType;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
public class CommentResponseDTO {

    private Long commentId;
    private Long userId;

    private TargetType targetType;
    private Long targetId;

    private String content;
    private LocalDateTime createAt;

    public static CommentResponseDTO fromVO(CommentVO selectedCommentVO) {
        CommentResponseDTO commentResponseDTO = new CommentResponseDTO();

        commentResponseDTO.setCommentId(selectedCommentVO.getCommentId());
        commentResponseDTO.setUserId(selectedCommentVO.getUserId());
        commentResponseDTO.setTargetType(selectedCommentVO.getTargetType());
        commentResponseDTO.setTargetId(selectedCommentVO.getTargetId());
        commentResponseDTO.setContent(selectedCommentVO.getContent());
        commentResponseDTO.setCreateAt(selectedCommentVO.getCreateAt());

        return commentResponseDTO;
    }
}
