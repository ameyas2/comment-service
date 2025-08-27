package org.comment.service;

import lombok.extern.log4j.Log4j2;
import org.comment.dao.CommentDao;
import org.comment.dao.CommentDaoAsync;
import org.comment.http.PostServiceHTTP;
import org.comment.http.UserServiceHTTP;
import org.instancio.Instancio;
import org.instancio.Select;
import org.posts.dto.CommentDTO;
import org.posts.dto.PostDTO;
import org.posts.dto.UserDTO;
import org.posts.mapper.CommentMapper;
import org.posts.mapper.PostMapper;
import org.posts.mapper.UserMapper;
import org.posts.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
public class CommentService {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private CommentDaoAsync commentDaoAsync;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostServiceHTTP postServiceHTTP;

    @Autowired
    private UserServiceHTTP userServiceHTTP;

    public CommentDTO getCommentById(UUID id) {
        Comment comment = getComment(id);
        Post post = comment.getPost();
        CommentDTO commentDTO = commentMapper.toCommentDto(comment);
        commentDTO.setPostDTO(postMapper.toPostDTO(post));
        return commentDTO;
    }

    public CommentDTO deleteCommentById(UUID id) {
        log.info("Deleting comment with id: {}", id);
        if(commentDao.exists(id)) {
            commentDao.deleteCommentById(id);
            return CommentDTO.builder().message("Comment deleted with id " + id).build();
        } else {
            return CommentDTO.builder().message("Comment not found with id " + id).build();
        }
    }

    public List<CommentDTO> getCommentsByPostId(UUID postId) {
        List<Comment> comments = commentDao.getCommentsByPostId(postId);
        return comments.stream()
                .map(comment -> commentMapper.toCommentDto(comment))
                .toList();
    }

    public CommentDTO updateComment(CommentDTO commentDTO) {
        Optional<Comment> optionalComment = commentDao.getCommentById(commentDTO.getId());
        if(optionalComment.isEmpty()) {
            return CommentDTO.builder()
                    .message("Comment does not exists for id : " + commentDTO.getId())
                    .build();
        }
        Comment comment = optionalComment.get();
        updateComment(comment, commentDTO);
        commentDao.saveComment(comment);
        log.info("Comment Updated with id : {}", comment.getId());
        return commentMapper.toCommentDto(comment);
    }

    private void updateComment(Comment comment, CommentDTO commentDTO) {
        comment.setDescription(commentDTO.getDescription());
    }

    public CommentDTO getRandomComment() {
        return commentMapper.toCommentDto(commentDao.getAnyComment().orElse(null));
    }

    public CommentDTO addRandomComment() {
        log.info("Adding Random comment");
        Comment comment = Instancio.of(Comment.class)
                .generate(Select.field("description"),
                        gen -> gen.string().minLength(50).mixedCase())
                .ignore(Select.field("post"))
                .ignore(Select.field(AbstractEntity.class, "id"))
                .ignore(Select.field(AbstractEntity.class, "createdAt"))
                .ignore(Select.field(AbstractEntity.class, "updatedAt"))
                .create();
        log.info("Created Random comment : {}", comment.getId());
        PostDTO postDTO = postServiceHTTP.getRandomPost();
        Post post = postMapper.toPost(postDTO);
        comment.setPost(post);
        log.info("Set Post : {}", post.getId());
        commentDao.saveComment(comment);
        log.info("Added new Comment with id: {}", comment.getId());
        return commentMapper.toCommentDto(comment);
    }

    private Comment getComment(UUID id) {
        Optional<Comment> commentOptional = commentDao.getCommentById(id);
        if(commentOptional.isEmpty()) {
            log.info("No comment exists for the id : {}", id);
            CommentDTO.builder().message("No comment exists for the id : " + id).build();
        }
        return commentOptional.get();
    }


    public CommentDTO asyncCommentSave() {
        log.info("Adding Random comment");
        Comment comment = Instancio.of(Comment.class)
                .generate(Select.field("description"),
                        gen -> gen.string().minLength(50).mixedCase())
                .ignore(Select.field("post"))
                .ignore(Select.field(AbstractEntity.class, "id"))
                .ignore(Select.field(AbstractEntity.class, "createdAt"))
                .ignore(Select.field(AbstractEntity.class, "updatedAt"))
                .create();
        log.info("Created Random comment : {}", comment.getId());
        PostDTO postDTO = postServiceHTTP.getRandomPost();
        Post post = postMapper.toPost(postDTO);
        comment.setPost(post);
        log.info("Set Post : {}", post.getId());

        commentDaoAsync.jpaSave(comment);

        CompletableFuture<Comment> hazelcastSave = commentDaoAsync.hazelCastSave(comment);
        CompletableFuture<CommentRedis> redisSave = commentDaoAsync.redisSave(comment);
        CompletableFuture<CommentCassandra> cassandraSave = commentDaoAsync.cassandraSave(comment);

        CompletableFuture.allOf(hazelcastSave, redisSave, cassandraSave).join();

        log.info("Added new Comment with id: {}", comment.getId());
        return commentMapper.toCommentDto(comment);
    }


    /*private Collection<PostDTO> postDTOs(Collection<Post> posts) {
        return posts.stream().map(post -> postMapper.toPostDTO(post)).toList();
    }*/


}
