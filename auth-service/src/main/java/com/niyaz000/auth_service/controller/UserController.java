package com.niyaz000.auth_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.niyaz000.auth_service.response.UserGetResponse;
import com.niyaz000.auth_service.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserGetResponse getUserById(@PathVariable("id") String userId) {
        return userService.getUserById(userId);
    }

}
