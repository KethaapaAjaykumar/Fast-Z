package com.example.Social.Media.Platform.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String text;
    private String username;
    private Long userId;
    private LocalDateTime createdAt;
}
