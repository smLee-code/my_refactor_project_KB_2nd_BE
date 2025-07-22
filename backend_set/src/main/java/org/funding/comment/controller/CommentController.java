package org.funding.comment.controller;

import lombok.RequiredArgsConstructor;
import org.funding.comment.dto.DeleteCommentRequestDTO;
import org.funding.comment.dto.InsertCommentRequestDTO;
import org.funding.comment.dto.CommentResponseDTO;
import org.funding.comment.service.CommentService;
import org.funding.comment.vo.enumType.TargetType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("")
    public ResponseEntity<CommentResponseDTO> addComment(@RequestBody InsertCommentRequestDTO requestDTO) {
        CommentResponseDTO responseDTO = commentService.addComment(requestDTO);

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteComment(@RequestParam("commentId") Long commentId) {
        commentService.deleteComment(commentId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("")
    public ResponseEntity<List<CommentResponseDTO>> getAllComments(@RequestParam("targetType") TargetType targetType, @RequestParam("targetId") Long targetId) {
        List<CommentResponseDTO> commentList = commentService.findAllComments(targetType, targetId);

        return ResponseEntity.ok(commentList);
    }
}
