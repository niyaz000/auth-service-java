package com.niyaz000.auth_service.mapper;

import com.niyaz000.auth_service.entity.User;
import com.niyaz000.auth_service.response.UserGetResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    public static UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", source = "publicId")
    UserGetResponse toDto(User user);
}
