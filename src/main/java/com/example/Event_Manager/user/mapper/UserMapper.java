package com.example.Event_Manager.user.mapper;

import com.example.Event_Manager.user.User;
import com.example.Event_Manager.user.dto.request.UpdateUserDTO;
import com.example.Event_Manager.user.dto.response.UserDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget User user, UpdateUserDTO dto);
}