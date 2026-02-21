package com.example.Social.Media.Platform.service;

import com.example.Social.Media.Platform.dto.CommentDTO;
import com.example.Social.Media.Platform.model.Comment;
import com.example.Social.Media.Platform.model.Post;
import com.example.Social.Media.Platform.model.User;
import com.example.Social.Media.Platform.repository.CommentRepository;
import com.example.Social.Media.Platform.repository.PostRepository;
import com.example.Social.Media.Platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentDTO addComment(Long postId, String text) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .text(text)
                .user(user)
                .post(post)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return CommentDTO.builder()
                .id(savedComment.getId())
                .text(savedComment.getText())
                .username(user.getUsername())
                .userId(user.getId())
                .createdAt(savedComment.getCreatedAt())
                .build();
    }
}
