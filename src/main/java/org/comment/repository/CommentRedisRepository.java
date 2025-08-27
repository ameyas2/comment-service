package org.comment.repository;

import org.posts.model.Comment;
import org.posts.model.CommentRedis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRedisRepository extends CrudRepository<CommentRedis, UUID> {

}
