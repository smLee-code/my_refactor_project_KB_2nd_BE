package org.funding.comment.service;

import lombok.RequiredArgsConstructor;
import org.funding.badge.service.BadgeService;
import org.funding.comment.dao.CommentDAO;
import org.funding.comment.dto.CommentResponseDTO;
import org.funding.comment.dto.DeleteCommentRequestDTO;
import org.funding.comment.dto.InsertCommentRequestDTO;
import org.funding.comment.vo.CommentVO;
import org.funding.comment.vo.enumType.TargetType;
import org.funding.fund.dao.FundDAO;
import org.funding.fund.vo.FundVO;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.CommentException;
import org.funding.project.dao.ProjectDAO;
import org.funding.project.vo.ProjectVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentDAO commentDAO;
    private final BadgeService badgeService;
    private final FundDAO fundDAO;
    private ProjectDAO projectDAO;

    public CommentResponseDTO addComment(InsertCommentRequestDTO requestDTO, Long userId) {
        CommentVO commentVO = requestDTO.toVO();
        commentVO.setUserId(userId);
        commentDAO.insertComment(commentVO);
        Long commentId = commentVO.getCommentId();

        CommentVO commentById = commentDAO.getCommentById(commentId);

        // 뱃지 권한 부여
        badgeService.checkAndGrantBadges(userId);

        // 프로젝트, 펀딩에 관한 댓글 등록 뱃지 권한 부여
        if (requestDTO.getTargetType() == TargetType.Funding) {
            FundVO fund = fundDAO.selectById(requestDTO.getTargetId());
            Long projectId = fund.getProjectId();
            ProjectVO project = projectDAO.selectProjectById(projectId);
            Long projectUserId = project.getUserId();
            badgeService.checkAndGrantBadges(projectUserId);
        }

        if (requestDTO.getTargetType() == TargetType.Project) {
            ProjectVO project = projectDAO.selectProjectById(requestDTO.getTargetId());
            Long projectUserId = project.getUserId();
            badgeService.checkAndGrantBadges(projectUserId);
        }

        return CommentResponseDTO.fromVO(commentById);
    }

    public void deleteComment(Long commentId) {

        CommentVO commentById = commentDAO.getCommentById(commentId);

        if (commentById == null) {
            throw new CommentException(ErrorCode.NOT_FOUND_COMMENT);
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
