//package org.example.daiam.service;
//
//import org.example.daiam.dto.request.CreatePermissionRequest;
//import org.example.daiam.dto.request.DeletePermissionRequest;
//import org.example.daiam.dto.request.UpdatePermissionRequest;
//
//import org.example.daiam.entity.Permission;
//
//import org.example.daiam.entity.Scope;
//import org.example.daiam.exception.SaveToDatabaseFailedException;
//import org.example.daiam.repo.PermissionRepo;
//import org.example.daiam.repo.RolePermissionRepo;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.example.model.dto.response.Response;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class PermissionService {
//    private final PermissionRepo permissionRepo;
//    private final RolePermissionRepo rolePermissionRepo;
//
//    public Permission findById(String id){
//        return permissionRepo.findById(UUID.fromString(id)).orElseThrow(()->new IllegalArgumentException("Permission not found"));
//    }
//    public Response<?> create(CreatePermissionRequest request) {
//        log.info("-------------------------------" + SecurityContextHolder.getContext().getAuthentication().getName() + " create permission");
//        String name = request.resourceName();
//        if (permissionRepo.existsByResourceName(name)) {
//            throw new IllegalArgumentException("Resource name existed");
//        }
//        try {
//            Permission permission = Permission.builder()
//                    .resourceCode(request.resourceCode())
//                    .resourceName(request.resourceName())
//                    .scope(request.scope())
//                    .build();
//            permissionRepo.save(permission);
//            return Response.created("Create permission successful", permissionRepo.findByResourceNameIgnoreCase(name).orElseThrow());
//        } catch (Exception ex) {
//            log.error(ex.getMessage());
//            log.info("-------------------------------" + SecurityContextHolder.getContext().getAuthentication().getName() + "failed create permission");
//            throw new SaveToDatabaseFailedException("Create permission failed: " + ex.getMessage());
//        }
//
//    }
//
//    @Transactional
//    public Response<?> updateById(UpdatePermissionRequest request) {
//        UUID id = UUID.fromString(request.permissionId());
//        String resourceName = request.resourceName();
//        Scope scope = request.scope();
//        String resourceCode = request.resourceCode();
//        boolean deleted = request.deleted();
//        if (!permissionRepo.existsByPermissionId(id)) {//kiem tra co ton tai ko
//            throw new IllegalArgumentException("Permission id not found");
//        } else if (permissionRepo.existsPermissionsByResourceCodeAndResourceNameAndScopeAndPermissionIdNot(resourceCode, resourceName, scope, id)) {//kiem tra co trung permission khac ko
//            throw new IllegalArgumentException("Permission field existed");
//        }
//        try {
//            Permission permission = permissionRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("HERE"));
//            permission.setDeleted(deleted);
//            permission.setScope(scope);
//            permission.setResourceName(resourceName);
//            permission.setResourceCode(resourceCode);
//            permissionRepo.save(permission);
//            // this will not work with auditorAware isOperationSuccess(permissionRepo.updatePermissionById(id, resourceCode, scope, resourceName, deleted), "Update permission failed");//update va kiem tra permission
//            rolePermissionRepo.updateResourceCodeAndScopeByPermissionId(resourceCode, scope, id);//update lai role_permission
//            return Response.success("Update successful", permissionRepo.findByResourceNameIgnoreCase(resourceName).orElseThrow());
//        } catch (Exception ex) {
//            throw new IllegalArgumentException("Update permission failed: " + ex
//                    .getMessage());
//        }
//    }
////    @Transactional
////    public Response<?> updateByResourceName(UpdatePermissionRequest request) {
////        String resourceName = request.resourceName();
////        String scope = request.scope();
////        String resourceCode = request.resourceCode();
////        boolean deleted = request.deleted();
////        if (!permissionRepo.existsByResourceName(resourceName)) {
////            throw new IllegalArgumentException("Permission resource name not found");
////        } else if (permissionRepo.existsPermissionsByResourceCodeAndScopeAndResourceNameNot(resourceCode, resourceName, scope)) {
////            throw new IllegalArgumentException("Permission field existed");
////        }
////        try {
////            isOperationSuccess(permissionRepo.updatePermissionByResourceName(resourceCode, scope, resourceName, deleted), "Update permission failed");
////            return new Response().success("Update successful", permissionRepo.findByResourceNameIgnoreCase(resourceName).orElseThrow());
////        } catch (Exception ex) {
////            throw new IllegalArgumentException("Update permission failed");
////        }
////    }
//
//    @Transactional
//    public Response<?> deleteById(DeletePermissionRequest request) {
//        UUID id = UUID.fromString(request.permissionId());
//        if (permissionRepo.existsByPermissionId(id)) {
//            throw new IllegalArgumentException("Permission id not found");
//        }
//        try {
//            isOperationSuccess(permissionRepo.deletePermissionById(id), "Delete permission failed");
//            return Response.success("Deleted successful", permissionRepo.findById(id).orElseThrow());
//        } catch (Exception ex) {
//            throw new IllegalArgumentException("Delete permission failed");
//        }
//    }
//
//    private void isOperationSuccess(int isSuccess, String message) {
//        if (isSuccess == 0) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    @Value("${permission.scopes}")
//    private String scopes;
//
//    public List<String> getScopes() {
//        return List.of(scopes.split("\\|"));
//    }
//}
