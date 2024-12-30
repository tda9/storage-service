package org.example.daiam.domain;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRole{
    private UUID id;
    private UUID userId;
    private UUID roleId;
    private Boolean deleted;

    public UserRole(UUID userId, UUID roleId){
        id = UUID.randomUUID();
        this.userId = userId;
        this.roleId = roleId;
        deleted = false;
    }
}
