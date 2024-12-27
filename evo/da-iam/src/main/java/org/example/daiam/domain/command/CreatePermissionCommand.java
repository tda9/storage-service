package org.example.daiam.domain.command;

import lombok.Getter;
import lombok.Setter;
import org.example.daiam.infrastruture.support.Scope;
@Getter
@Setter
public class CreatePermissionCommand {
    private String resourceCode;
    private String resourceName;
    private Scope scope;
}
