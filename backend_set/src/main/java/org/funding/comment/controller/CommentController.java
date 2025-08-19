package org.funding.comment.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.funding.comment.dto.DeleteCommentRequestDTO;
import org.funding.comment.dto.InsertCommentRequestDTO;
import org.funding.comment.dto.CommentResponseDTO;
import org.funding.comment.service.CommentService;
import org.funding.comment.vo.enumType.TargetType;
import org.funding.security.util.Auth;
import org.funding.user.vo.MemberVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "댓글 API")
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @ApiOperation(value = "댓글 작성", notes = "특정 대상(펀딩, 프로젝트 등)에 새로운 댓글을 작성합니다.")
    @Auth
    @PostMapping("")
    public ResponseEntity<CommentResponseDTO> addComment(@RequestBody InsertCommentRequestDTO requestDTO,
                                                         HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        CommentResponseDTO responseDTO = commentService.addComment(requestDTO, userId);
        return ResponseEntity.ok(responseDTO);
    }

    @ApiOperation(value = "댓글 삭제", notes = "commentId를 기준으로 특정 댓글을 삭제합니다.")
    @Auth
    @DeleteMapping("")
    public ResponseEntity<Void> deleteComment(
            @ApiParam(value = "삭제할 댓글 ID", required = true, example = "1") @RequestParam("commentId") Long commentId,
            HttpServletRequest request) {

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "댓글 목록 조회", notes = "특정 대상(펀딩, 프로젝트 등)에 달린 모든 댓글 목록을 조회합니다.")
    @Auth
    @GetMapping("")
    public ResponseEntity<List<CommentResponseDTO>> getAllComments(
            @ApiParam(value = "댓글 대상 타입", required = true, example = "Funding") @RequestParam("targetType") TargetType targetType,
            @ApiParam(value = "댓글 대상 ID", required = true, example = "1") @RequestParam("targetId") Long targetId,
            HttpServletRequest request) {

        List<CommentResponseDTO> commentList = commentService.findAllComments(targetType, targetId);
        return ResponseEntity.ok(commentList);
    }
}