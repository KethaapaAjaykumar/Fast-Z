package com.example.Social.Media.Platform.repository;

import com.example.Social.Media.Platform.model.Like;
import com.example.Social.Media.Platform.model.Post;
import com.example.Social.Media.Platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);

    boolean existsByUserAndPost(User user, Post post);

    long countByPostId(Long postId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(l) FROM Like l WHERE l.post.user.id = :userId")
    long countLikesByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);
}
