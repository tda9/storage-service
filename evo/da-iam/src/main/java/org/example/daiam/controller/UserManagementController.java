package org.example.daiam.controller;


import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.example.daiam.controller.factory.UserServiceFactory;
import org.example.daiam.dto.request.CreateUserRequest;
import org.example.daiam.dto.request.FilterUsersRequest;
import org.example.daiam.dto.request.UpdateUserRequest;
import org.example.daiam.dto.response.UserDtoResponse;
import org.example.daiam.entity.Role;
import org.example.daiam.entity.User;
import org.example.daiam.repo.RoleRepo;
import org.example.daiam.repo.impl.UserRepoImpl;
import org.example.daiam.service.ExcelService;
import org.example.daiam.service.impl.AuthorityServiceImpl;
import org.example.daiam.service.impl.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.model.UserAuthority;
import org.example.model.dto.response.BasedResponse;
import org.example.model.dto.response.PageResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserManagementController {
    private final UserServiceFactory userServiceFactory;
    private final UserService userService;
    private final RoleRepo userRoleRepo;
    private final ExcelService excelService;
    private final AuthorityServiceImpl authorityServiceImpl;
    private final UserRepoImpl userRepoImpl;

    @PreAuthorize("hasPermission('USERS','CREATE')")
    @PostMapping("/create")
    public BasedResponse<?> create(@RequestBody @Valid CreateUserRequest request) {
        return BasedResponse.created("Create successful", userServiceFactory.getUserService().create(request));
    }

    @PreAuthorize("hasPermission('USERS','UPDATE')")
    @PutMapping("/{id}/update")
    public BasedResponse<?> updateById(
            @PathVariable @NotBlank String id,
            @RequestBody @Valid UpdateUserRequest request) {
        return BasedResponse.success("Update successful", userServiceFactory.getUserService().updateById(request,id));
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
        int totalPage;
        if (currentSize >= totalSize&& totalSize!=0) {
            currentSize = Math.toIntExact(totalSize);
            totalPage = ((int) (totalSize / currentSize));
        }else{
            totalPage = 0;
            currentSize = 0;
        }
        return new PageResponse<>(currentPage, totalPage, currentSize, totalSize, sortBy, sort, users);
    }
    @PreAuthorize("hasPermission('USERS','READ')")
    @GetMapping("/filter")
    public BasedResponse<?> filter(
            @ModelAttribute FilterUsersRequest request,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "email") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort
    ) {
        List<User> users = userService.filter(request, sortBy, sort, currentSize, currentPage);
        Long totalSize = userService.getTotalFilterSize(request);
        int totalPage;
        if (currentSize >= totalSize&& totalSize!=0) {
            currentSize = Math.toIntExact(totalSize);
            totalPage = ((int) (totalSize / currentSize));
        }else{
            totalPage = 0;
            currentSize = 0;
        }
        return new PageResponse<>(currentPage, totalPage, currentSize, totalSize, sortBy, sort, users);
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

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportUsers(
            @ModelAttribute FilterUsersRequest request,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "1") int currentSize,
            @RequestParam(required = false, defaultValue = "email") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sort) {
        try {
            List<User> users = userRepoImpl.filterByField(request,sortBy,sort,currentSize,currentPage);
            // Generate Excel file as byte array
            byte[] excelFile = excelService.writeUsersToExcel(users);
            // Set HTTP headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=users.xlsx");
            headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return new ResponseEntity<>(excelFile, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/import")
    public ResponseEntity<?> importUsers(@RequestParam("file") MultipartFile file) {
        String msg = excelService.importExcelData(file);
        return ResponseEntity.ok(msg);
    }
    @GetMapping("/{username}/authorities-by-username")
    BasedResponse<UserAuthority> getUserAuthority(
            @PathVariable String username) {
        return BasedResponse.success("Get authorities successful for " + username, authorityServiceImpl.getUserAuthority(username));
    }
    @GetMapping("/{clientId}/authorities-by-clientId")
    BasedResponse<UserAuthority> getClientAuthority(
            @PathVariable UUID clientId) {
        return BasedResponse.success("Get authorities successful for " + clientId,
                authorityServiceImpl.getClientAuthority(clientId));//TODO: use factory pattern to get keycloak authoriry
    }
}
