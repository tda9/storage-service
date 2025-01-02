package org.example.daiam.domain;

import lombok.*;
import org.apache.commons.lang.StringUtils;
import org.example.daiam.audit.entity.AuditDomain;
import org.example.daiam.audit.entity.AuditEntity;
import org.example.daiam.domain.command.CreatePermissionCommand;
import org.example.daiam.domain.command.UpdatePermissionCommand;
import org.example.daiam.infrastruture.support.Scope;

import java.util.UUID;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends AuditDomain {

    private UUID permissionId;
    private String resourceName;
    private Scope scope;
    private String resourceCode;
    private boolean deleted;

    public Permission(CreatePermissionCommand cmd){
        this.permissionId = UUID.randomUUID();
        this.resourceName = cmd.getResourceName();
        this.resourceCode = cmd.getResourceCode();
        this.scope = cmd.getScope();
    }

    public void update(UpdatePermissionCommand cmd) {
        if (StringUtils.isNotBlank(cmd.getResourceName())) {
            this.setResourceName(cmd.getResourceName());
        }
        if (cmd.getDeleted() != null) {
            this.setDeleted(cmd.getDeleted());
        }
        if (cmd.getScope() != null) {
            this.setScope(cmd.getScope());
        }
    }

    public void delete() {
        this.setDeleted(true);
    }
}
