package org.example.daiam.domain;

import lombok.*;
import org.example.daiam.domain.command.CreatePermissionCommand;
import org.example.daiam.infrastruture.support.Scope;

import java.util.UUID;


@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    private UUID permissionId;
    private String resourceName;
    private Scope scope;
    private String resourceCode;
    private boolean deleted = false;

    public Permission(CreatePermissionCommand cmd){
        this.permissionId = UUID.randomUUID();
        this.resourceName = cmd.getResourceName();
        this.resourceCode = cmd.getResourceCode();
    }

}
