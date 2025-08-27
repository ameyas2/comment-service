package org.comment.repository;

import org.posts.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Query("select c.id from Comment c")
    List<UUID> getAllIds();

    @Query("SELECT c from Comment c JOIN Post p on p.id = c.post.id where p.id = :postId")
    List<Comment> getCommentsByPostId(UUID postId);
}
