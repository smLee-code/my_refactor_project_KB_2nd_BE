package org.funding.comment.service;

import lombok.RequiredArgsConstructor;
import org.funding.comment.dao.CommentDAO;
import org.funding.comment.dto.CommentResponseDTO;
import org.funding.comment.dto.DeleteCommentRequestDTO;
import org.funding.comment.dto.InsertCommentRequestDTO;
import org.funding.comment.vo.CommentVO;
import org.funding.comment.vo.enumType.TargetType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentDAO commentDAO;

    public CommentResponseDTO addComment(InsertCommentRequestDTO requestDTO) {
        CommentVO commentVO = requestDTO.toVO();
        commentDAO.insertComment(commentVO);
        Long commentId = commentVO.getCommentId();

        CommentVO commentById = commentDAO.getCommentById(commentId);
        return CommentResponseDTO.fromVO(commentById);
    }

    public void deleteComment(Long commentId) {

        CommentVO commentById = commentDAO.getCommentById(commentId);

        if (commentById == null) {
            // 삭제할 댓글이 없음.
            return;
        }

        commentDAO.deleteComment(commentId);
    }

    public List<CommentResponseDTO> findAllComments(TargetType targetType, Long targetId) {

        List<CommentVO> commentVOList = null;

        switch (targetType) {
            case Project:
                commentVOList = commentDAO.getAllCommentsByProjectId(targetId);
                break;

            case Funding:
                commentVOList = commentDAO.getAllCommentsByFundingId(targetId);
                break;
        }

        return commentVOList.stream()
                .map(CommentResponseDTO::fromVO)
                .collect(Collectors.toList());


    }
}
