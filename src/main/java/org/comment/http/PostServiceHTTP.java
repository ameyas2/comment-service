package org.comment.http;

import org.posts.dto.PostDTO;
import org.posts.dto.UserDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.UUID;

@HttpExchange("/post-service/api/posts")
public interface PostServiceHTTP {

    @GetExchange("/{id}")
    PostDTO getPostById(@PathVariable UUID id);

    @GetExchange("/load")
    PostDTO getRandomPost();

}
