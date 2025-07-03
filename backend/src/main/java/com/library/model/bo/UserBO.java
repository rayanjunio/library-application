package com.library.model.bo;

import com.library.exception.type.BusinessException;
import com.library.model.dao.LoanDAO;
import com.library.model.dao.ProfileDAO;
import com.library.model.dao.UserDAO;
import com.library.model.dto.log.LogDTO;
import com.library.model.dto.user.ChangePasswordDTO;
import com.library.model.dto.user.UserRequestDTO;
import com.library.model.dto.user.UserUpdateDTO;
import com.library.model.dto.user.UserResponseDTO;
import com.library.model.mapper.UserMapper;
import com.library.model.entity.Profile;
import com.library.model.entity.User;
import com.library.model.enums.UserStatus;
import com.library.security.JwtContext;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
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
  LogBO logBO;

  @Inject
  JwtContext jwtContext;

  public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
    Optional<User> userExists = userDAO.findByEmail(userRequestDTO.getEmail());

    if (userExists.isPresent()) {
      logBO.create(new LogDTO("POST /users", -1L, 400, LocalDateTime.now()));
      throw new BusinessException("This email already is registered", 400);
    }

    if (userDAO.findByCpf(userRequestDTO.getCpf()).isPresent()) {
      logBO.create(new LogDTO("POST /users", -1L, 400, LocalDateTime.now()));
      throw new BusinessException("This CPF already is registered", 400);
    }

    User user = mapper.toEntity(userRequestDTO);
    Optional<Profile> profileExists;

    if (userDAO.count() == 0) {
      profileExists = profileDAO.findByRole("ADMIN");

      if (profileExists.isEmpty()) {
        logBO.create(new LogDTO("POST /users", -1L, 400, LocalDateTime.now()));
        throw new BusinessException("ADMIN profile does not exist", 400);
      }
      user.setProfile(profileExists.get());
    } else {
      profileExists = profileDAO.findByRole("MEMBER");

      if (profileExists.isEmpty()) {
        logBO.create(new LogDTO("POST /users", -1L, 400, LocalDateTime.now()));
        throw new BusinessException("MEMBER profile does not exist", 400);
      }
      user.setProfile(profileExists.get());
    }

    user.setPassword(BcryptUtil.bcryptHash(userRequestDTO.getPassword()));
    user.setStatus(UserStatus.ACTIVE);
    userDAO.save(user);

    logBO.create(new LogDTO("POST /users", user.getId(), 201, LocalDateTime.now()));

    return mapper.toDTO(user);
  }

  public UserResponseDTO createAdminUser(UserRequestDTO userRequestDTO) {
    Optional<User> userExists = userDAO.findByEmail(userRequestDTO.getEmail());

    if (userExists.isPresent()) {
      throw new BusinessException("This email already is registered", 400);
    }

    if (userDAO.findByCpf(userRequestDTO.getCpf()).isPresent()) {
      throw new BusinessException("This CPF already is registered", 400);
    }

    User user = mapper.toEntity(userRequestDTO);

    Profile profile = profileDAO.findByRole("ADMIN")
            .orElseThrow(() -> new BusinessException("ADMIN profile does not exist", 400));
    user.setProfile(profile);

    user.setPassword(BcryptUtil.bcryptHash(userRequestDTO.getPassword()));
    user.setStatus(UserStatus.ACTIVE);
    userDAO.save(user);

    return mapper.toDTO(user);
  }

  public UserResponseDTO getUser(long id) {
    if (!jwtContext.getUserRole().equals("[ADMIN]") && jwtContext.getUserId() != id) {
      throw new BusinessException("You are forbidden to access this content", 403);
    }

    User user = userDAO.findByIdWithLoans(id)
            .orElseThrow(() -> new BusinessException("User not found", 404));
    return mapper.toDTO(user);
  }

  @Transactional
  public List<UserResponseDTO> getAllUsers() {
    return userDAO.findAll().stream()
            .map(mapper::toDTO)
            .collect(Collectors.toList());
  }

  public UserResponseDTO updateUser(long id, UserUpdateDTO userUpdateDTO) {
    if (jwtContext.getUserId() != id) {
      throw new BusinessException("You are forbidden to access this content", 403);
    }

    User user = userDAO.findById(id);
    if (user == null) {
      throw new BusinessException("User not found", 404);
    }

    // Atualiza apenas os campos que foram fornecidos
    if (userUpdateDTO.getName() != null) {
      user.setName(userUpdateDTO.getName());
    }
    if (userUpdateDTO.getEmail() != null) {
      Optional<User> existingUser = userDAO.findByEmail(userUpdateDTO.getEmail());

      if (existingUser.isPresent() && existingUser.get().getId() != id) {
        throw new BusinessException("This email is already registered", 400);
      }
      user.setEmail(userUpdateDTO.getEmail());
    }

    if (userUpdateDTO.getCpf() != null) {
      Optional<User> existingUser = userDAO.findByCpf(userUpdateDTO.getCpf());

      if (existingUser.isPresent() && existingUser.get().getId() != id) {
        throw new BusinessException("This CPF is already registered", 400);
      }

      user.setCpf(userUpdateDTO.getCpf());
    }

    userDAO.merge(user);
    return mapper.toDTO(user);
  }

  public void deleteUser(long id) {
    if (jwtContext.getUserId() != id) {
      throw new BusinessException("You are forbidden to access this content", 403);
    }

    if (loanDAO.countUserPendingLoans(id) != 0) {
      throw new BusinessException("It is not possible, because this user has pending loans", 400);
    }

    User user = userDAO.findById(id);
    if (user == null) {
      throw new BusinessException("User not found", 404);
    }

    userDAO.delete(id);
  }

  public void changePassword(ChangePasswordDTO dto) {
    User user = userDAO.findById(jwtContext.getUserId());

    if (user == null || !BcryptUtil.matches(dto.getCurrentPassword(), user.getPassword())) {
      throw new BusinessException("Invalid credentials", 400);
    }

    if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
      throw new BusinessException("The new password does not match the confirmation", 400);
    }

    if (BcryptUtil.matches(dto.getNewPassword(), user.getPassword())) {
      throw new BusinessException("New password must be different from the current one", 400);
    }

    user.setPassword(BcryptUtil.bcryptHash(dto.getNewPassword()));
    userDAO.merge(user);
  }
}
