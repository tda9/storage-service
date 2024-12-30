package org.example.daiam.application.dto.request;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.model.dto.request.PagingRequest;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchExactUserRequest extends PagingRequest {
    private String userId;
    private String email;
    private String username;
    private Boolean isRoot;
    private Boolean isLock;
    private Boolean isVerified;
    private Boolean deleted;
    private Integer experience;
    private Integer stt;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDate dob;
    private String street;
    private String ward;
    private String province;
    private String district;
}