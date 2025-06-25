package com.library.model.bo;

import com.library.model.dao.ProfileDAO;
import com.library.model.dto.profile.ProfileRequestDTO;
import com.library.model.dto.profile.ProfileResponseDTO;
import com.library.model.mapper.ProfileMapper;
import com.library.model.entity.Profile;
import com.library.model.enums.Role;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProfileBO {
  @Inject
  ProfileDAO profileDAO;

  @Inject
  ProfileMapper mapper;

  public ProfileResponseDTO createProfile(ProfileRequestDTO profileRequestDTO) {
    if(profileRequestDTO.getRole() != Role.ADMIN && profileRequestDTO.getRole() != Role.MEMBER) {
      throw new IllegalArgumentException("Only ADMIN and MEMBER roles are valid");
    }

    if(profileDAO.findByRole(profileRequestDTO.getRole().name()).isPresent()) {
      throw new IllegalArgumentException("This role already exists");
    }

    Profile profile = mapper.toEntity(profileRequestDTO);
    profileDAO.save(profile);

    return mapper.toDTO(profile);
  }
}
