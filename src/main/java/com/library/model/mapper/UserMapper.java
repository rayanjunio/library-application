package com.library.model.mapper;

import com.library.model.dto.user.UserRequestDTO;
import com.library.model.dto.user.UserResponseDTO;
import com.library.model.entity.User;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserMapper {

  public User toEntity(UserRequestDTO userRequestDTO) {
    User user = new User();
    user.setName(userRequestDTO.getName());
    user.setEmail(userRequestDTO.getEmail());
    user.setPassword(userRequestDTO.getPassword());
    user.setCpf(userRequestDTO.getCpf());
    return user;
  }

  public UserResponseDTO toDTO(User user) {
    UserResponseDTO userResponseDTO = new UserResponseDTO();
    userResponseDTO.setId(user.getId());
    userResponseDTO.setName(user.getName());
    userResponseDTO.setEmail(user.getEmail());
    userResponseDTO.setCpf(user.getCpf());
    userResponseDTO.setStatus(user.getStatus());
    userResponseDTO.setProfileId(user.getProfile().getId());
    return userResponseDTO;
  }
}