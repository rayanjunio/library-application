package com.library.model.bo;

import com.library.exception.type.BusinessException;
import com.library.model.dao.ProfileDAO;
import com.library.model.dto.profile.ProfileRequestDTO;
import com.library.model.dto.profile.ProfileResponseDTO;
import com.library.model.mapper.ProfileMapper;
import com.library.model.entity.Profile;
import com.library.model.enums.Role;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class ProfileBO {
  @Inject
  ProfileDAO profileDAO;

  @Inject
  ProfileMapper mapper;

  @Transactional
  public ProfileResponseDTO createProfile(ProfileRequestDTO profileRequestDTO) {
    if(profileRequestDTO.getRole() != Role.ADMIN && profileRequestDTO.getRole() != Role.MEMBER) {
      throw new BusinessException("Only ADMIN and MEMBER roles are valid", 400);
    }

    if(profileDAO.findByRole(profileRequestDTO.getRole().name()).isPresent()) {
      throw new BusinessException("This role already exists", 400);
    }

    Profile profile = mapper.toEntity(profileRequestDTO);
    profileDAO.save(profile);

    return mapper.toDTO(profile);
  }
}
