package com.example.Social.Media.Platform.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String bio;
    private String profilePictureUrl;
    private String location;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
