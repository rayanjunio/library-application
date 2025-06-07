package com.library.bo;

import com.library.dao.ProfileDAO;
import com.library.dao.UserDAO;
import com.library.dto.request.UserRequestDTO;
import com.library.dto.response.UserResponseDTO;
import com.library.mapper.UserMapper;
import com.library.model.Profile;
import com.library.model.User;
import com.library.model.UserStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class UserBO {
  @Inject
  UserDAO userDAO;

  @Inject
  UserMapper mapper;

  @Inject
  ProfileDAO profileDAO;

  public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
    Optional<User> userExists = userDAO.findByEmail(userRequestDTO.getEmail());

    if(userExists.isPresent()) {
      throw new IllegalArgumentException("This email already is registered");
    }

    if(userDAO.findByCpf(userRequestDTO.getCpf()).isPresent()) {
      throw new IllegalArgumentException("This CPF already is registered");
    }

    User user = mapper.toEntity(userRequestDTO);
    Profile profile;

    if(userDAO.count() == 0) {
      profile = profileDAO.findByRole("ADMIN")
              .orElseThrow(() -> new RuntimeException("ADMIN does not exist"));
      user.setProfile(profile);
    } else {
      profile = profileDAO.findByRole("MEMBER")
              .orElseThrow(() -> new RuntimeException("ADMIN does not exist"));
      user.setProfile(profile);
    }

    user.setStatus(UserStatus.ACTIVE);

    userDAO.save(user);

    return mapper.toDTO(user);
  }
}
