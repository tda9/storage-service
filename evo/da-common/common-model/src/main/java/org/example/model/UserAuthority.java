package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuthority {
    private UUID userId;
    private String email;
    private String password;
    private boolean isLocked;
    private boolean isDeleted;
    private boolean isVerified;
    private Boolean isRoot;
    private List<String> grantedPermissions;
}
