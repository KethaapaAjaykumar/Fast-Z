package com.example.Social.Media.Platform.controller;

import com.example.Social.Media.Platform.dto.CommentDTO;
import com.example.Social.Media.Platform.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long postId, @RequestBody String text) {
        return ResponseEntity.ok(commentService.addComment(postId, text));
    }
}
