package org.comment.controller;

import lombok.extern.log4j.Log4j2;
import org.comment.service.CommentService;
import org.posts.dto.CommentDTO;
import org.posts.dto.PostDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@Log4j2
public class CommentController {

    @Autowired
    private CommentService commentService;

    @DeleteMapping("/{id}")
    public ResponseEntity<CommentDTO> deleteComment(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(commentService.deleteCommentById(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getComment(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    @PutMapping("/")
    public ResponseEntity<CommentDTO> updateComment(@RequestBody CommentDTO commentDTO) {
        return ResponseEntity.ok(commentService.updateComment(commentDTO));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Collection<CommentDTO>> getCommentsByPostId(@PathVariable("postId") UUID postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }


    @PostMapping("/load")
    public ResponseEntity<CommentDTO> addRandomComment() {
        return ResponseEntity.ok(commentService.addRandomComment());
    }

    @GetMapping("/load")
    public ResponseEntity<CommentDTO> getRandomComment() {
        return ResponseEntity.ok(commentService.getRandomComment());
    }

    @PostMapping("/load/async")
    public ResponseEntity<CommentDTO> addRandomCommentAsync() {
        return ResponseEntity.ok(commentService.asyncCommentSave());
    }
}
