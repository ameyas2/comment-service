package org.comment.dao;

import com.hazelcast.core.HazelcastInstance;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.comment.repository.CommentCassandraRepository;
import org.comment.repository.CommentRedisRepository;
import org.comment.repository.CommentRepository;
import org.posts.model.Comment;
import org.posts.model.CommentCassandra;
import org.posts.model.CommentRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@Log4j2
public class CommentDao {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentRedisRepository commentRedisRepository;

    @Autowired
    private CommentCassandraRepository commentCassandraRepository;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    public Optional<Comment> getCommentById(UUID id) {
        return commentRepository.findById(id);
    }

    public List<Comment> getCommentsByPostId(UUID postId) {
        return commentRepository.getCommentsByPostId(postId);
    }

    private Map<UUID, Comment> comments;

    @PostConstruct
    private void init() {
        this.comments = hazelcastInstance.getMap("comments");
        log.info("Total comment ids loaded : {}", comments.size());
    }

    public long count() {
        return commentRepository.count();
    }

    public Optional<Comment> getAnyComment() {
        Optional<Comment> comment =  comments.values().stream().findAny();
        return comment;
    }

    public Comment saveComment(Comment comment) {
        commentRepository.save(comment);
        log.info("DB saved : {}", comment);
        comments.put(comment.getId(), comment);
        log.info("Hazel cast saved : {}", comment.getId());
        CommentRedis commentRedis = commentRedisRepository.save(toCommentRedis(comment));
        log.info("Redis saved : {}", commentRedis.getId());
        CommentCassandra commentCassandra = commentCassandraRepository.save(toCommentCassandra(comment));
        log.info("Cassandra saved : {}", commentCassandra.getId());
        return comment;
    }

    private CommentCassandra toCommentCassandra(Comment comment) {
        return CommentCassandra.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .description(comment.getDescription())
                .postId(comment.getPost().getId())
                .build();
    }

    private CommentRedis toCommentRedis(Comment comment) {
        return CommentRedis.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .description(comment.getDescription())
                .build();
    }

    public boolean exists(UUID id) {
        return commentRepository.existsById(id);
    }

    public void deleteCommentById(UUID id) {
        log.debug("Deleting post with id: {}", id);
        commentRepository.deleteById(id);
    }
}
