package com.example.mutsamarket.controller;

import com.example.mutsamarket.dto.commentDto.CommentEnrollDto;
import com.example.mutsamarket.dto.commentDto.CommentsReadDto;
import com.example.mutsamarket.dto.commentDto.ReplyDto;
import com.example.mutsamarket.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/mutsamarket")
@RestController
@Slf4j
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/items/{itemId}/comments")
    public ResponseEntity<Map<String, String>> enroll(
            @Valid @RequestBody CommentEnrollDto dto,
            @PathVariable("itemId") Long itemId,
            Authentication authentication
    ) {
        commentService.enrollComment(dto, itemId, authentication);

        log.info(dto.toString());
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "댓글이 등록되었습니다.");

        return ResponseEntity.ok(responseBody);

    }

    @GetMapping("/items/{itemId}/comments")
    public Page<CommentsReadDto>  readCommentsPage(
            @RequestParam(value = "page", defaultValue = "0") Long page,
            @PathVariable("itemId") Long itemId
    ){
        return commentService.readCommentsPage(page, itemId);
    }

    @PutMapping("/items/{itemId}/comments/{commentId}")
    public ResponseEntity<Map<String, String>> updateComment(
            @PathVariable("itemId") Long itemId,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentEnrollDto dto,
            Authentication authentication
    ) {
        commentService.updateComment(itemId, commentId, dto, authentication);

        log.info(dto.toString());
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "댓글이 수정되었습니다.");

        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/items/{itemId}/comments/{commentId}/reply")
    public ResponseEntity<Map<String, String>> Reply(
            @PathVariable("itemId") Long itemId,
            @PathVariable("commentId") Long commentId,
            @RequestBody ReplyDto dto,
            Authentication authentication
    ) {
        if(commentService.addReply(itemId, commentId, dto, authentication) == 1) {

            log.info(dto.toString());
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "댓글에 답변이 추가되었습니다.");

            return ResponseEntity.ok(responseBody);
        }
        if(commentService.addReply(itemId, commentId, dto, authentication) == 2) {

            log.info(dto.toString());
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "답변이 수정되었습니다.");

            return ResponseEntity.ok(responseBody);
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/items/{itemId}/comments/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @PathVariable("itemId") Long itemId,
            @PathVariable("commentId") Long commentId,
            Authentication authentication
    ) {
        if (commentService.deleteComment(itemId, commentId, authentication)) {

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "댓글을 삭제했습니다.");

            return ResponseEntity.ok(responseBody);
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

}
