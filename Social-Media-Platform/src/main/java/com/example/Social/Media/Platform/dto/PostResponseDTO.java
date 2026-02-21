package com.example.Social.Media.Platform.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDTO {
    private Long id;
    private String content;
    private String imageUrl;
    private String username;
    private Long userId;
    private LocalDateTime createdAt;
    private long likeCount;
    private List<CommentDTO> comments;
}
