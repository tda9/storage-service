package org.example.daiam.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.model.dto.request.PagingRequest;

import java.time.LocalDate;

@Getter
@Setter
public class FilterUsersRequest extends PagingRequest {
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
