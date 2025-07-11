package org.comment.http;

import org.posts.dto.UserDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.UUID;

@HttpExchange("/user-service/api/users")
public interface UserServiceHTTP {

    @GetExchange("/{id}")
    UserDTO getUserById(@PathVariable UUID id);

    @GetExchange("/load")
    UserDTO getRandomUser();

}
