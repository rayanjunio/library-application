package com.library.bo;

import com.library.dao.LoanDAO;
import com.library.dao.ProfileDAO;
import com.library.dao.UserDAO;
import com.library.dto.request.user.UserRequestDTO;
import com.library.dto.request.user.UserUpdateDTO;
import com.library.dto.response.UserResponseDTO;
import com.library.mapper.UserMapper;
import com.library.model.Profile;
import com.library.model.User;
import com.library.model.UserStatus;
import com.library.security.JwtContext;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserBO {
  @Inject
  UserDAO userDAO;

  @Inject
  ProfileDAO profileDAO;

  @Inject
  LoanDAO loanDAO;

  @Inject
  UserMapper mapper;

  @Inject
  JwtContext jwtContext;

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

    user.setPassword(BcryptUtil.bcryptHash(userRequestDTO.getPassword()));
    user.setStatus(UserStatus.ACTIVE);
    userDAO.save(user);

    return mapper.toDTO(user);
  }

  public UserResponseDTO getUser(long id) {
    if(jwtContext.getUserId() != id) {
      throw new IllegalArgumentException("You are forbidden to access this content");
    }

    User user = userDAO.findById(id);
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }
    return mapper.toDTO(user);
  }

  public List<UserResponseDTO> getAllUsers() {
    return userDAO.findAll().stream()
            .map(mapper::toDTO)
            .collect(Collectors.toList());
  }

  public UserResponseDTO updateUser(long id, UserUpdateDTO userUpdateDTO) {
    if(jwtContext.getUserId() != id) {
      throw new IllegalArgumentException("You are forbidden to access this content");
    }

    User user = userDAO.findById(id);
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }

    // Atualiza apenas os campos que foram fornecidos
    if (userUpdateDTO.getName() != null) {
      user.setName(userUpdateDTO.getName());
    }
    if (userUpdateDTO.getEmail() != null) {
      Optional<User> existingUser = userDAO.findByEmail(userUpdateDTO.getEmail());

      if (existingUser.isPresent() && existingUser.get().getId() != id) {
        throw new IllegalArgumentException("This email is already registered");
      }
      user.setEmail(userUpdateDTO.getEmail());
    }

    if (userUpdateDTO.getCpf() != null) {
      Optional<User> existingUser = userDAO.findByCpf(userUpdateDTO.getCpf());

      if (existingUser.isPresent() && existingUser.get().getId() != id) {
        throw new IllegalArgumentException("This CPF is already registered");
      }

      user.setCpf(userUpdateDTO.getCpf());
    }

    userDAO.merge(user);
    return mapper.toDTO(user);
  }

  public void deleteUser(long id) {
    if(jwtContext.getUserId() != id) {
      throw new IllegalArgumentException("You are forbidden to access this content");
    }

    if(loanDAO.countUserPendingLoans(id) != 0) {
      throw new IllegalArgumentException("It is not possible, because this user has pending loans");
    }

    User user = userDAO.findById(id);
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }

    userDAO.delete(id);
  }
}
