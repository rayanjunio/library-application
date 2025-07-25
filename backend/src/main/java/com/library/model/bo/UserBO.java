package com.library.model.bo;

import com.library.exception.type.BusinessException;
import com.library.model.dao.UserDAO;
import com.library.model.dto.PagedResponseDTO;
import com.library.model.dto.user.ChangePasswordDTO;
import com.library.model.dto.user.UserRequestDTO;
import com.library.model.dto.user.UserUpdateDTO;
import com.library.model.dto.user.UserResponseDTO;
import com.library.model.entity.Profile;
import com.library.model.entity.User;
import com.library.model.enums.Role;
import com.library.model.enums.UserStatus;
import com.library.security.JwtContext;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@RequestScoped
public class UserBO {
  @Inject
  UserDAO userDAO;

  @Inject
  LoanBO loanBO;

  @Inject
  ProfileBO profileBO;

  @Inject
  JwtContext jwtContext;

  @Transactional
  public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
    Optional<User> userExists = userDAO.findByEmail(userRequestDTO.getEmail());

    if (userExists.isPresent()) {
      throw new BusinessException("Este e-mail já está cadastrado", 400);
    }

    if (userDAO.findByCpf(userRequestDTO.getCpf()).isPresent()) {
      throw new BusinessException("Este CPF já está cadastrado", 400);
    }

    User user = new User(userRequestDTO);
    Profile profile;

    if (userDAO.count() == 0) profile = profileBO.getOrCreateProfileByRole("ADMIN");
     else profile = profileBO.getOrCreateProfileByRole("MEMBER");

    user.setProfile(profile);
    user.setPassword(BcryptUtil.bcryptHash(userRequestDTO.getPassword()));
    user.setStatus(UserStatus.ACTIVE);
    userDAO.save(user);

    return new UserResponseDTO(user);
  }

  @Transactional
  public UserResponseDTO createAdminUser(UserRequestDTO userRequestDTO) {
    Optional<User> userExists = userDAO.findByEmail(userRequestDTO.getEmail());

    if (userExists.isPresent()) {
      throw new BusinessException("Este e-mail já está cadastrado", 400);
    }

    if (userDAO.findByCpf(userRequestDTO.getCpf()).isPresent()) {
      throw new BusinessException("Este CPF já está cadastrado", 400);
    }

    User user = new User(userRequestDTO);
    Profile profile = profileBO.getOrCreateProfileByRole("ADMIN");

    user.setProfile(profile);
    user.setPassword(BcryptUtil.bcryptHash(userRequestDTO.getPassword()));
    user.setStatus(UserStatus.ACTIVE);
    userDAO.save(user);

    return new UserResponseDTO(user);
  }

  public UserResponseDTO getUserWithLoans() {
    long userId = jwtContext.getUserId();

    User user = userDAO.findByIdWithLoans(userId)
            .orElseThrow(() -> new BusinessException("Usuário não encontrado", 404));

    return new UserResponseDTO(user);
  }

  public UserResponseDTO getUser() {
    long userId = jwtContext.getUserId();

    Optional<User> user = userDAO.findUser(userId);
    if(user.isEmpty()) {
      throw new BusinessException("Usuário não encontrado", 404);
    }

    return new UserResponseDTO(user.get());
  }

  @Transactional
  public PagedResponseDTO<UserResponseDTO> getAllUsers(int page, int size) {
    List<User> allUsers = userDAO.findAllUsers(page, size);
    long total = userDAO.countAllUsers();

    List<UserResponseDTO> allUsersResponse = allUsers
            .stream()
            .map(UserResponseDTO::new)
            .toList();
    return new PagedResponseDTO<>(total, page, size, allUsersResponse);
  }

  @Transactional
  public UserResponseDTO updateUser(UserUpdateDTO userUpdateDTO) {
    long userId = jwtContext.getUserId();

    User user = userDAO.findById(userId);
    if (user == null) {
      throw new BusinessException("Usuário não encontrado", 404);
    }

    // Atualiza apenas os campos que foram fornecidos
    if (userUpdateDTO.getName() != null) {
      user.setName(userUpdateDTO.getName());
    }
    if (userUpdateDTO.getEmail() != null) {
      Optional<User> existingUser = userDAO.findByEmail(userUpdateDTO.getEmail());

      if (existingUser.isPresent() && existingUser.get().getId() != userId) {
        throw new BusinessException("Este e-mail já está cadastrado", 400);
      }
      user.setEmail(userUpdateDTO.getEmail());
    }

    if (userUpdateDTO.getCpf() != null) {
      Optional<User> existingUser = userDAO.findByCpf(userUpdateDTO.getCpf());

      if (existingUser.isPresent() && existingUser.get().getId() != userId) {
        throw new BusinessException("Este CPF já está cadastrado", 400);
      }

      user.setCpf(userUpdateDTO.getCpf());
    }

    userDAO.merge(user);
    return new UserResponseDTO(user);
  }

  @Transactional
  public void deleteUser() {
    long userId = jwtContext.getUserId();

    User user = userDAO.findById(userId);
    if (user == null) {
      throw new BusinessException("Usuário não encontrado", 404);
    }

    if(user.getProfile().getRole().equals(Role.ADMIN) && userDAO.countAdminUsers() == 1) {
      throw new BusinessException("O sistema não pode ficar sem um administrador. Adicione outro administrador antes de excluir sua conta", 400);
    }

    if (loanBO.countUserPendingLoans(userId) != 0) {
      throw new BusinessException("Não é possível excluir esta conta. O usuário possui empréstimos pendentes", 400);
    }

    userDAO.delete(user);
  }

  @Transactional
  public void changePassword(ChangePasswordDTO dto) {
    long userId = jwtContext.getUserId();
    User user = userDAO.findById(userId);

    if (user == null || !BcryptUtil.matches(dto.getCurrentPassword(), user.getPassword())) {
      throw new BusinessException("Credenciais inválidas", 400);
    }

    if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
      throw new BusinessException("A nova senha não coincide com a confirmação", 400);
    }

    if (BcryptUtil.matches(dto.getNewPassword(), user.getPassword())) {
      throw new BusinessException("A nova senha deve ser diferente da atual", 400);
    }

    user.setPassword(BcryptUtil.bcryptHash(dto.getNewPassword()));
    userDAO.merge(user);
  }

  public long countAllUsers() {
    return userDAO.countAllUsers();
  }
}