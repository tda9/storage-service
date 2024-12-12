package org.example.daiam.dto.response;

import org.example.daiam.entity.Role;

import lombok.Builder;
import org.example.daiam.entity.User;

import java.util.Set;

@Builder
public record UserDtoResponse(
        User user,
        Set<Role> roles
) {

}
