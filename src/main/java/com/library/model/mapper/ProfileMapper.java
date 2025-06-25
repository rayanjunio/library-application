package com.library.model.mapper;

import com.library.model.dto.profile.ProfileRequestDTO;
import com.library.model.dto.profile.ProfileResponseDTO;
import com.library.model.entity.Profile;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProfileMapper {
  public Profile toEntity(ProfileRequestDTO profileRequestDTO) {
    Profile profile = new Profile();
    profile.setRole(profileRequestDTO.getRole());
    return profile;
  }

  public ProfileResponseDTO toDTO(Profile profile) {
    if (profile == null) {
      return null;
    }
    return new ProfileResponseDTO(profile.getId(), profile.getRole());
  }
}
