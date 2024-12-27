package org.example.daiam.domain.command;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class CreateRoleCommand {
    private String name;
    private List<UUID> permissionsIds;
}
