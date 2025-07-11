package org.comment.dao;

import com.hazelcast.core.HazelcastInstance;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.comment.repository.CommentRepository;
import org.posts.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Log4j2
public class CommentDao {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    public Optional<Comment> getCommentById(UUID id) {
        return commentRepository.findById(id);
    }

    public List<Comment> getCommentsByPostId(UUID postId) {
        return commentRepository.getCommentsByPostId(postId);
    }

    public List<Comment> getCommentsByUserId(UUID userId) {
        return commentRepository.getCommentsByUserId(userId);
    }

    private List<UUID> commentIds;

    @PostConstruct
    private void init() {
        this.commentIds = hazelcastInstance.getList("comment_ids");
        if(this.commentIds.isEmpty()) {
            List<UUID> ids = commentRepository.getAllIds();
            this.commentIds.addAll(ids);
        }
        log.info("Total comment ids loaded : {}", commentIds.size());
    }

    public long count() {
        return commentRepository.count();
    }

    public UUID getCommentId(int index) {
        return commentIds.get(index);
    }

    public Comment saveComment(Comment comment) {
        commentRepository.save(comment);
        commentIds.add(comment.getId());
        return comment;
    }

    public boolean exists(UUID id) {
        return commentRepository.existsById(id);
    }

    public void deleteCommentById(UUID id) {
        log.debug("Deleting post with id: {}", id);
        commentRepository.deleteById(id);
        commentIds.remove(id);
    }
}
