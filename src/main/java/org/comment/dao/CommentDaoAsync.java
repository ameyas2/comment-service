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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Repository
@Log4j2
public class CommentDaoAsync {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentRedisRepository commentRedisRepository;

    @Autowired
    private CommentCassandraRepository commentCassandraRepository;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    private Map<UUID, Comment> comments;

    @PostConstruct
    private void init() {
        this.comments = hazelcastInstance.getMap("comments");
        log.info("Total comment ids loaded async : {}", comments.size());
    }

    public Comment jpaSave(Comment comment) {
        commentRepository.save(comment);
        log.info("DB saved : {}", comment);
        return comment;
    }

    @Async
    public CompletableFuture<Comment> hazelCastSave(Comment comment) {
        try {
            log.info("Saving to Hazel cast : {}", comment.getId());
            return CompletableFuture.completedFuture(comments.put(comment.getId(), comment));
        } catch (Exception e) {
            log.error("Exception : {}", e.getStackTrace());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    public CompletableFuture<CommentRedis> redisSave(Comment comment) {
        try {
            log.info("Saving to Redis : {}", comment.getId());
            return CompletableFuture.completedFuture(commentRedisRepository.save(toCommentRedis(comment)));
        } catch (Exception e) {
            log.error("Exception : {}", e.getStackTrace());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    public CompletableFuture<CommentCassandra> cassandraSave(Comment comment) {
        try {
            log.info("Saving to Cassandra : {}", comment.getId());
            return CompletableFuture.completedFuture(commentCassandraRepository.save(toCommentCassandra(comment)));
        } catch (Exception e) {
            log.error("Exception : {}", e.getStackTrace());
            return CompletableFuture.failedFuture(e);
        }
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

    private CommentCassandra toCommentCassandra(Comment comment) {
        return CommentCassandra.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .description(comment.getDescription())
                .postId(comment.getPost().getId())
                .build();
    }

}
