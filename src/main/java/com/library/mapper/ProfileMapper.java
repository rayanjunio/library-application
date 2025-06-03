package com.library.mapper;

import com.library.dto.request.ProfileRequestDTO;
import com.library.dto.response.ProfileResponseDTO;
import com.library.model.Profile;
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
