package com.library.bo;

import com.library.dao.ProfileDAO;
import com.library.dao.UserDAO;
import com.library.dto.request.UserRequestDTO;
import com.library.dto.request.UserUpdateDTO;
import com.library.dto.response.UserResponseDTO;
import com.library.mapper.UserMapper;
import com.library.model.Profile;
import com.library.model.User;
import com.library.model.UserStatus;
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

  public UserResponseDTO getUser(long id) {
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

  public UserResponseDTO updateUser(long id, UserUpdateDTO userUpdateDTO, long authenticatedUserId) {
    User user = userDAO.findById(id);
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }

    // Buscar o perfil do usuário autenticado
    User authenticatedUser = userDAO.findById(authenticatedUserId);
    if (authenticatedUser == null) {
      throw new IllegalArgumentException("Authenticated user not found");
    }
    Profile authenticatedProfile = authenticatedUser.getProfile();

    // Se não for ADMIN, só pode atualizar o próprio perfil
    if (!authenticatedProfile.getRole().name().equals("ADMIN") && authenticatedUserId != id) {
      throw new IllegalArgumentException("You can only update your own profile");
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
    if (userUpdateDTO.getPassword() != null) {
      user.setPassword(userUpdateDTO.getPassword());
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

  public void deleteUser(long id, long authenticatedUserId) {
    User user = userDAO.findById(id);
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }

    // Buscar o perfil do usuário autenticado
    User authenticatedUser = userDAO.findById(authenticatedUserId);
    if (authenticatedUser == null) {
      throw new IllegalArgumentException("Authenticated user not found");
    }
    Profile authenticatedProfile = authenticatedUser.getProfile();

    // Se não for ADMIN, só pode deletar o próprio perfil
    if (!authenticatedProfile.getRole().name().equals("ADMIN") && authenticatedUserId != id) {
      throw new IllegalArgumentException("You can only delete your own profile");
    }

    userDAO.delete(id);
  }
}
