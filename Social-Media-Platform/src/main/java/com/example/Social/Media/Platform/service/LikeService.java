package com.example.Social.Media.Platform.service;

import com.example.Social.Media.Platform.model.Like;
import com.example.Social.Media.Platform.model.Post;
import com.example.Social.Media.Platform.model.User;
import com.example.Social.Media.Platform.repository.LikeRepository;
import com.example.Social.Media.Platform.repository.PostRepository;
import com.example.Social.Media.Platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public String toggleLike(Long postId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return likeRepository.findByUserAndPost(user, post)
                .map(like -> {
                    likeRepository.delete(like);
                    return "Unliked";
                })
                .orElseGet(() -> {
                    likeRepository.save(Like.builder().user(user).post(post).build());
                    return "Liked";
                });
    }
}
