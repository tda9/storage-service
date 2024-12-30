package org.example.daiam.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PermissionDto {
    private String permissionId;
    private String permissionName;
}
