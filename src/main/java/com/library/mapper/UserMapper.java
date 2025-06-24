package com.library.mapper;

import com.library.dto.request.user.UserRequestDTO;
import com.library.dto.response.UserResponseDTO;
import com.library.model.User;
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