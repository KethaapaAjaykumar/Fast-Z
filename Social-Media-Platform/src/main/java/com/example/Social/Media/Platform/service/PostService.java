package com.example.Social.Media.Platform.service;

import com.example.Social.Media.Platform.dto.CommentDTO;
import com.example.Social.Media.Platform.dto.PostResponseDTO;
import com.example.Social.Media.Platform.model.Post;
import com.example.Social.Media.Platform.model.User;
import com.example.Social.Media.Platform.repository.LikeRepository;
import com.example.Social.Media.Platform.repository.PostRepository;
import com.example.Social.Media.Platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

        private final PostRepository postRepository;
        private final UserRepository userRepository;
        private final LikeRepository likeRepository;
        private final ExternalApiService externalApiService;

        @Transactional
        @CacheEvict(value = "posts", allEntries = true)
        public PostResponseDTO createPost(String content, String imageUrl) {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Post post = Post.builder()
                                .content(content)
                                .imageUrl(imageUrl)
                                .user(user)
                                .build();

                Post savedPost = postRepository.save(post);

                // Example of External API Integration
                externalApiService.notifyExternalSystem("New post created by " + user.getUsername()).subscribe();

                return mapToPostResponseDTO(savedPost);
        }

        @Cacheable(value = "posts")
        public Page<PostResponseDTO> getAllPosts(Pageable pageable) {
                return postRepository.findAll(pageable)
                                .map(this::mapToPostResponseDTO);
        }

        public Page<PostResponseDTO> getPostsByUserId(Long userId, Pageable pageable) {
                return postRepository.findByUserId(userId, pageable)
                                .map(this::mapToPostResponseDTO);
        }

        public PostResponseDTO getPostById(Long id) {
                Post post = postRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Post not found"));
                return mapToPostResponseDTO(post);
        }

        @Transactional
        @CacheEvict(value = "posts", allEntries = true)
        public void deletePost(Long id) {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Post post = postRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Post not found"));

                if (!post.getUser().getId().equals(user.getId()) && !user.getRole().name().equals("ROLE_ADMIN")) {
                        throw new RuntimeException("You are not authorized to delete this post");
                }

                postRepository.delete(post);
        }

        public PostResponseDTO mapToPostResponseDTO(Post post) {
                return PostResponseDTO.builder()
                                .id(post.getId())
                                .content(post.getContent())
                                .imageUrl(post.getImageUrl())
                                .username(post.getUser().getUsername())
                                .userId(post.getUser().getId())
                                .createdAt(post.getCreatedAt())
                                .likeCount(likeRepository.countByPostId(post.getId()))
                                .comments(post.getComments().stream()
                                                .map(comment -> CommentDTO.builder()
                                                                .id(comment.getId())
                                                                .text(comment.getText())
                                                                .username(comment.getUser().getUsername())
                                                                .userId(comment.getUser().getId())
                                                                .createdAt(comment.getCreatedAt())
                                                                .build())
                                                .collect(Collectors.toList()))
                                .build();
        }
}
