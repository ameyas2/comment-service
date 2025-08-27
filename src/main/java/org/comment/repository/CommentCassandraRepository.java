package org.comment.repository;

import org.posts.model.Comment;
import org.posts.model.CommentCassandra;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CommentCassandraRepository extends CassandraRepository<CommentCassandra, UUID> {
}
