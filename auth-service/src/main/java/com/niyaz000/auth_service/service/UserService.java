package com.niyaz000.auth_service.service;

import org.springframework.stereotype.Service;

import com.niyaz000.auth_service.entity.EntityType;
import com.niyaz000.auth_service.exception.EntityNotFoundException;
import com.niyaz000.auth_service.repository.UserRepository;
import com.niyaz000.auth_service.response.UserGetResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserGetResponse getUserById(String userId) {
        userRepository.findByPublicId(userId)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.USERS, "id", userId));
        return null;
    }
}
