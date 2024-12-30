package org.example.daiam.application.dto.response;//package org.example.demo.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class UserDto{
    private String userId;
    private String email;
    private List<UUID> roleIds;

}
