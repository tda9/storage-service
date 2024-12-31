package org.example.daiam.domain.command;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.daiam.annotation.ValidScope;
import org.example.daiam.infrastruture.support.Scope;

@Getter
@Setter
@AllArgsConstructor
public class UpdatePermissionCommand {
    private String resourceName;
    private Scope scope;
    private String resourceCode;
    private Boolean deleted;
}
