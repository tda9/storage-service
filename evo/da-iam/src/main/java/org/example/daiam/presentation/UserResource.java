package org.example.daiam.presentation;


import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.example.client.storage.StorageClient;
import org.example.daiam.application.dto.request.CreateUserRequest;
import org.example.daiam.application.dto.request.SearchExactUserRequest;
import org.example.daiam.application.dto.request.SearchKeywordUserRequest;
import org.example.daiam.application.dto.request.UpdateUserRequest;
import org.example.daiam.application.dto.response.UserDto;
import org.example.daiam.application.service.UserQueryService;
import org.example.daiam.application.service.others.ExcelService;
import org.example.daiam.application.service.impl.AuthorityServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.daiam.domain.User;
import org.example.daiam.presentation.factory.UserServiceFactory;
import org.example.web.support.MessageUtils;
import org.example.model.UserAuthority;
import org.example.model.dto.response.Response;
import org.example.model.dto.response.PageResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserResource {
    private final ExcelService excelService;
    private final AuthorityServiceImpl authorityServiceImpl;
    private final StorageClient storageClient;
    private final UserServiceFactory userCommandService;
    private final UserQueryService userQueryService;

    @PreAuthorize("hasPermission(null,'user.read')")
    @GetMapping("/users/{id}")
    public Response<User> get(@PathVariable String id) {
        return Response.success(MessageUtils.USER_FOUND_BY_ID_MESSAGE, userQueryService.getById(id));
    }

    @PreAuthorize("hasPermission(null,'user.create')")
    @PostMapping("/users/create")
    public Response<User> create(@RequestBody @Valid CreateUserRequest request) {
        return Response.created(MessageUtils.CREATE_USER_REQUEST_SUCCESSFUL_MESSAGE, userCommandService.getUserService().create(request));
    }

    @PreAuthorize("hasPermission(null,'user.update')")
    @PutMapping("/users/{id}/update")
    public Response<User> update(
            @PathVariable @NotBlank String id,
            @RequestBody @Valid UpdateUserRequest request) {
        return Response.success(MessageUtils.UPDATE_USER_REQUEST_SUCCESSFUL_MESSAGE,
                userCommandService.getUserService().update(request, id));
    }

    @PreAuthorize("hasPermission(null,'user.read')")
    @GetMapping("/users/search-keyword")
    public Response<List<UserDto>> searchKeyword(@ModelAttribute @Valid SearchKeywordUserRequest request) {
        List<UserDto> users = userQueryService.searchKeyword(request);
        Long totalSize = userQueryService.getTotalSize(request);
        return PageResponse.of(request, users, totalSize);
    }

    @PreAuthorize("hasPermission(null,'user.read')")
    @GetMapping("/users/search-exact")
    public Response<List<UserDto>> searchExact(@ModelAttribute SearchExactUserRequest request) {
        List<UserDto> users = userQueryService.searchExact(request);
        Long totalSize = userQueryService.getTotalSize(request);
        return PageResponse.of(request, users, totalSize);
    }

    @GetMapping("/users/export")
    public ResponseEntity<byte[]> export(@ModelAttribute @Valid SearchExactUserRequest request) {
        // Generate Excel file as byte array
        byte[] excelFile = excelService.writeUserEntitysToExcel(request);
        // Set HTTP headers for file download
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=users.xlsx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return new ResponseEntity<>(excelFile, headers, HttpStatus.OK);
    }

    @PostMapping("/users/import/{userId}")
    public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file, @PathVariable String userId) {
        String msg = excelService.importExcelData(file);
        storageClient.saveImportExcelHistory(new MultipartFile[]{file}, userId);
        if (StringUtils.isNotBlank(msg)) {
            return ResponseEntity.ok(Response.success("Import successful", null));}
        return ResponseEntity.badRequest().body(Response.badRequest(msg, null));
    }

    @GetMapping("/{username}/authorities-by-username")
    Response<UserAuthority> getUserAuthority(
            @PathVariable String username) {
        return Response.success("Get authorities successful for " + username, authorityServiceImpl.getUserAuthority(username));
    }

    @GetMapping("/{clientId}/authorities-by-clientId")
    Response<UserAuthority> getClientAuthority(
            @PathVariable UUID clientId) {
        return Response.success("Get authorities successful for " + clientId,
                authorityServiceImpl.getClientAuthority(clientId));//TODO: use factory pattern to get keycloak authoriry
    }

    @GetMapping("/users/{userId}/impersonate/{otherId}")
    Response<Boolean> impersonate(
            @PathVariable @NotBlank String userId,
            @PathVariable @NotBlank String otherId
    ) {
        return Response.success("Get authorities successful for " + userId, authorityServiceImpl.impersonate(userId, otherId));
    }
}
