package org.funding.comment.controller;

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

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Auth
    @PostMapping("")
    public ResponseEntity<CommentResponseDTO> addComment(@RequestBody InsertCommentRequestDTO requestDTO,
                                                         HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        CommentResponseDTO responseDTO = commentService.addComment(requestDTO, userId);

        return ResponseEntity.ok(responseDTO);
    }

    @Auth
    @DeleteMapping("")
    public ResponseEntity<Void> deleteComment(@RequestParam("commentId") Long commentId, HttpServletRequest request) {
        commentService.deleteComment(commentId);

        return ResponseEntity.noContent().build();
    }

    @Auth
    @GetMapping("")
    public ResponseEntity<List<CommentResponseDTO>> getAllComments(@RequestParam("targetType") TargetType targetType,
                                                                   @RequestParam("targetId") Long targetId,
                                                                   HttpServletRequest request) {
        List<CommentResponseDTO> commentList = commentService.findAllComments(targetType, targetId);

        return ResponseEntity.ok(commentList);
    }
}
