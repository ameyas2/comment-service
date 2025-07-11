package org.comment.service;

import lombok.extern.log4j.Log4j2;
import org.comment.dao.CommentDao;
import org.comment.http.PostServiceHTTP;
import org.comment.http.UserServiceHTTP;
import org.posts.dto.CommentDTO;
import org.posts.dto.PostDTO;
import org.posts.dto.UserDTO;
import org.posts.mapper.CommentMapper;
import org.posts.mapper.PostMapper;
import org.posts.mapper.UserMapper;
import org.posts.model.Comment;
import org.posts.model.Post;
import org.posts.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Log4j2
public class CommentService {

    @Autowired
    private CommentDao commentDao;

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
        User user = comment.getUser();
        Post post = comment.getPost();
        CommentDTO commentDTO = commentMapper.toCommentDto(comment);
        commentDTO.setUserDTO(userMapper.toUserDTO(user));
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

    public CommentDTO getRandomComment() {
        Random random = new Random();
        int index = (int)random.nextLong(commentDao.count());
        UUID commentId = commentDao.getCommentId(index);
        return commentMapper.toCommentDto(getComment(commentId));
    }

    public List<CommentDTO> getCommentsByPostId(UUID postId) {
        List<Comment> comments = commentDao.getCommentsByPostId(postId);
        return comments.stream()
                .map(comment -> commentMapper.toCommentDto(comment))
                .toList();
    }

    public List<CommentDTO> getCommentsByUserId(UUID userId) {
        List<Comment> comments = commentDao.getCommentsByUserId(userId);
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


    public CommentDTO addRandomComment() {
        Random random = new Random();
        String description = generateRandomSentences(random.nextInt(10,41));
        //String username = generateRandomSentences(random.nextInt(2));
        PostDTO postDTO = postServiceHTTP.getRandomPost();
        Post post = postMapper.toPost(postDTO);

        UserDTO userDTO = userServiceHTTP.getUserById(postDTO.getUserDTO().getId());
        User user = userMapper.toUser(userDTO);

        Comment comment = Comment.of(description,post, user);
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

    private String generateRandomSentences(int wordCount) {
        StringBuilder sentence = new StringBuilder();
        for(int i = 0; i < wordCount; i++) {
            sentence.append(generateRandomWord() + " ");
        }
        return sentence.toString().trim();
    }

    private String generateRandomWord() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        int wordLength = random.nextInt(5, 15);
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < wordLength; i++) {
            builder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return builder.toString();
    }

    /*private Collection<PostDTO> postDTOs(Collection<Post> posts) {
        return posts.stream().map(post -> postMapper.toPostDTO(post)).toList();
    }*/


}
