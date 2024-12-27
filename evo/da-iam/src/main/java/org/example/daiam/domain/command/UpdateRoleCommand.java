package org.example.daiam.domain.command;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class UpdateRoleCommand {
    private UUID roleId;
    private String name;
    private List<UUID> permissionIds;
    private Boolean deleted;
}
