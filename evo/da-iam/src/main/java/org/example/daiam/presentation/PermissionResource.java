package org.example.daiam.presentation;


import jakarta.validation.constraints.NotBlank;
import org.example.daiam.application.dto.request.CreatePermissionRequest;
import org.example.daiam.application.dto.request.UpdatePermissionRequest;
import org.example.daiam.application.service.PermissionCommandService;
import org.example.daiam.application.service.PermissionQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.model.dto.response.Response;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PermissionResource {
    private final PermissionCommandService permissionCommandService;
    private final PermissionQueryService permissionQueryService;
    @PreAuthorize("hasPermission(null,'permission.create')")
    @PostMapping("/permissions/create")
    public Response<?> create(@RequestBody @Valid CreatePermissionRequest request) {
        return Response.success("Created",permissionCommandService.create(request));
    }
    @PreAuthorize("hasPermission(null,'permission.update')")
    @PutMapping("/permissions/{id}/update")
    public Response<?> updateById(
            @PathVariable @NotBlank String id,
            @RequestBody @Valid UpdatePermissionRequest request) {
        return Response.success("Updated",permissionCommandService.updateById(id,request));
    }
    @PreAuthorize("hasPermission(null,'permission.delete')")
    @DeleteMapping("/permissions/{id}/delete")
    public Response<?> deleteById(@PathVariable @NotBlank String id) {
        return Response.success("Deleted", permissionCommandService.deleteById(id));
    }
    @PreAuthorize("hasPermission(null,'permission.read')")
    @GetMapping("/permissions/{id}")
    public Response<?> findById(@PathVariable String id) {
        return Response.success("Found",permissionQueryService.getById(id));
    }
}
