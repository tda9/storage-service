package org.example.daiam.controller;


import org.example.daiam.controller.factory.UserServiceFactory;
import org.example.daiam.dto.request.CreateUserRequest;
import org.example.daiam.dto.request.UpdateUserRequest;
import org.example.daiam.dto.response.BasedResponse;
import org.example.daiam.dto.response.PageResponse;
import org.example.daiam.dto.response.UserDtoResponse;
import org.example.daiam.entity.Role;


import org.example.daiam.entity.User;
import org.example.daiam.repo.RoleRepo;
import org.example.daiam.service.impl.AuthorityServiceImpl;
import org.example.daiam.service.impl.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.model.UserAuthority;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserManagementController {
    private final UserServiceFactory userServiceFactory;
    private final UserService userService;
    private final RoleRepo userRoleRepo;
    private final AuthorityServiceImpl authorityServiceImpl;

    @PreAuthorize("hasPermission('USERS','CREATE')")
    @PostMapping("/create")
    public BasedResponse<?> create(@RequestBody @Valid CreateUserRequest request) {
        return BasedResponse.created("Create successful", userServiceFactory.getUserService().create(request));
    }

    @PreAuthorize("hasPermission('USERS','UPDATE')")
    @PutMapping("/update")
    public BasedResponse<?> updateById(@RequestBody @Valid UpdateUserRequest request) {
        return BasedResponse.success("Update successful", userServiceFactory.getUserService().updateById(request));
    }
    @PreAuthorize("hasPermission('USERS','READ')")
    @GetMapping("/search")
    public BasedResponse<?> searchByKeyword(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "email") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort
    ) {
        List<User> users = userService.searchByKeyword(keyword, sortBy, sort, currentSize, currentPage);
        Long totalSize = userService.getTotalSize(keyword);
        if(currentSize>totalSize){
         currentSize = Math.toIntExact(totalSize);
        }
        return new PageResponse<>(currentPage, ((int) (totalSize / currentSize) ), currentSize, totalSize, sortBy, sort, users);
    }

    @GetMapping("/{id}")
    public BasedResponse<?> findById(@PathVariable String id) {
        User user = userService.findById(id);
        Set<Role> roles = userRoleRepo.findRolesByUserId(user.getUserId());
        return BasedResponse.success("User found", UserDtoResponse.builder()
                .user(user)
                .roles(roles)
                .build());
    }

    @GetMapping("/absolute-search")
    public BasedResponse<?> absoluteSearch(
            @RequestParam(required = false) String keyword) {
        List<User> user = userService.searchByField(keyword);
        return BasedResponse.success("User found", user);
    }

    @GetMapping("/api/users/{userId}/authorities")
    ResponseEntity<UserAuthority> getUserAuthority(@PathVariable UUID userId) {
        return ResponseEntity.ok(authorityServiceImpl.getUserAuthority(userId));
    }

    @GetMapping("/api/users/{username}/authorities-by-username")
    ResponseEntity<UserAuthority> getUserAuthority(@PathVariable String username) {
        return ResponseEntity.ok(authorityServiceImpl.getUserAuthority(username));
    }
}
