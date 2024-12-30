package org.example.daiam.presentation;


import lombok.extern.slf4j.Slf4j;
import org.example.client.storage.StorageClient;
import org.example.daiam.application.dto.request.CreateUserRequest;
import org.example.daiam.application.dto.request.SearchExactUserRequest;
import org.example.daiam.application.dto.request.SearchKeywordUserRequest;
import org.example.daiam.application.dto.request.UpdateUserRequest;
import org.example.daiam.application.dto.response.UserDto;
import org.example.daiam.application.service.UserCommandService;
import org.example.daiam.application.service.UserQueryService;
import org.example.daiam.service.ExcelService;
import org.example.daiam.service.impl.AuthorityServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.daiam.utils.InputUtils;
import org.example.model.UserAuthority;
import org.example.model.dto.response.BasedResponse;
import org.example.model.dto.response.PageResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @GetMapping("/users/{id}")
    public BasedResponse<?> getById(@PathVariable String id) {
        return BasedResponse.success(InputUtils.USER_FOUND_BY_ID_MESSAGE, userQueryService.getById(id));
    }

    @PreAuthorize("hasPermission(null,'user.create')")
    @PostMapping("/users/create")
    public BasedResponse<?> create(@RequestBody @Valid CreateUserRequest request) {
        return BasedResponse.created(InputUtils.CREATE_USER_REQUEST_SUCCESSFUL_MESSAGE, userCommandService.create(request));
    }

    @PreAuthorize("hasPermission(null,'user.update')")
    @PutMapping("/users/{id}/update")
    public BasedResponse<?> updateById(
            @PathVariable String id,
            @RequestBody @Valid UpdateUserRequest request) {
        return BasedResponse.success(InputUtils.UPDATE_USER_REQUEST_SUCCESSFUL_MESSAGE, userCommandService.updateById(request, id));
    }

    @PreAuthorize("hasPermission(null,'user.read')")
    @GetMapping("/users/search-keyword")
    public BasedResponse<?> searchByKeyword(@ModelAttribute @Valid SearchKeywordUserRequest request) {
        List<UserDto> users = userQueryService.searchKeyword(request);
        Long totalSize = userQueryService.getTotalSize(request);
        return PageResponse.of(request, users, totalSize);
    }

    @PreAuthorize("hasPermission(null,'user.read')")
    @GetMapping("/users/search-exact")
    public BasedResponse<?> searchExact(@ModelAttribute SearchExactUserRequest request) {
        List<UserDto> users = userQueryService.searchExact(request);
        Long totalSize = userQueryService.getTotalSize(request);
        return PageResponse.of(request, users, totalSize);
    }

//    @GetMapping("/users/export")
//    public ResponseEntity<byte[]> export(
//            @ModelAttribute SearchExactUserRequest request,
//            @RequestParam(required = false, defaultValue = "1") int currentPage,
//            @RequestParam(required = false, defaultValue = "1") int currentSize,
//            @RequestParam(required = false, defaultValue = "email") String sortBy,
//            @RequestParam(required = false, defaultValue = "ASC") String sort) {
//        try {
//            List<User> users = userRepoImpl.filterByField(request);
//            // Generate Excel file as byte array
//            byte[] excelFile = excelService.writeUsersToExcel(users);
//            // Set HTTP headers for file download
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Content-Disposition", "attachment; filename=users.xlsx");
//            headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//
//            return new ResponseEntity<>(excelFile, headers, HttpStatus.OK);
//        } catch (IOException e) {
//            log.error(e.getMessage());
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @PostMapping("/users/import/{userId}")
//    public ResponseEntity<?> import(
//            @RequestParam("file") MultipartFile file,
//            @PathVariable String userId) {
//        String msg = excelService.importExcelData(file);
//        storageClient.saveImportExcelHistory(new MultipartFile[]{file}, userId);
//        if (msg == null || msg.isEmpty()) {
//            return ResponseEntity.ok(BasedResponse.success("Import successful", null));
//        } else {
//            return ResponseEntity.badRequest().body(BasedResponse.badRequest(msg, null));
//        }
//    }

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
