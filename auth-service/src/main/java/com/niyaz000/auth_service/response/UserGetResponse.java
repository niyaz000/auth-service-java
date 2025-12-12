package com.niyaz000.auth_service.response;

import com.niyaz000.auth_service.enums.UserStatus;

import lombok.Data;

@Data
public class UserGetResponse {

    private String id;

    private String email;

    private String phoneNumber;

    private String firstName;

    private String lastName;

    private UserStatus status;

}
