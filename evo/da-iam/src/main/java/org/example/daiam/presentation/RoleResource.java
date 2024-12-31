package org.example.daiam.presentation;

import jakarta.validation.constraints.NotBlank;
import org.example.daiam.application.dto.request.CreateRoleRequest;
import org.example.daiam.application.dto.request.DeleteRoleRequest;
import org.example.daiam.application.dto.request.UpdateRoleRequest;
import org.example.daiam.application.service.RoleCommandService;
import org.example.daiam.application.service.RoleQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.model.dto.response.Response;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RoleResource {
    private final RoleCommandService roleCommandService;
    private final RoleQueryService roleQueryService;

    @PreAuthorize("hasPermission(null,'roles.create')")
    @PostMapping("/roles/create")
    public Response<?> create(@RequestBody @Valid CreateRoleRequest createRoleRequest) {
        return Response.success("Create successful", roleCommandService.create(createRoleRequest));
    }

    @PreAuthorize("hasPermission(null,'roles.update')")
    @PutMapping("/roles/{id}/update")
    public Response<?> updateById(@RequestBody @Valid UpdateRoleRequest request,
                                  @PathVariable @NotBlank String id) {
        return Response.success("Update successful", roleCommandService.updateById(request, id));
    }

    @PreAuthorize("hasPermission(null,'roles.delete')")
    @DeleteMapping("/roles/{id}/delete")
    public Response<?> deleteById(
            @PathVariable @NotBlank String id,
            @RequestBody @Valid DeleteRoleRequest request) {
        return Response.success("Delete successful", roleCommandService.deleteById(request, id));
    }

    @PreAuthorize("hasPermission(null,'roles.read')")
    @GetMapping("/roles/{id}")
    public Response<?> getById(@PathVariable @NotBlank String id) {
        return Response.success("Found", roleQueryService.getById(id));
    }

}
