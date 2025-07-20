package com.library.model.bo;

import com.library.exception.type.BusinessException;
import com.library.model.dao.ProfileDAO;
import com.library.model.entity.Profile;
import com.library.model.enums.Role;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class ProfileBO {
  @Inject
  ProfileDAO profileDAO;

  @Transactional
  public Profile createAdminProfile() {
    Profile adminProfile = new Profile(Role.ADMIN);
    profileDAO.save(adminProfile);
    return adminProfile;
  }

  @Transactional
  public Profile createMemberProfile() {
    Profile memberProfile = new Profile(Role.MEMBER);
    profileDAO.save(memberProfile);
    return memberProfile;
  }

  public Profile getOrCreateProfileByRole(String role) {
    Profile profile = profileDAO.findByRole(role);

    if(profile == null) {
      if(role.equals("ADMIN")) return createAdminProfile();
      else if (role.equals("MEMBER")) return createMemberProfile();
      else throw new BusinessException("Invalid role", 400);
    }
    return profile;
  }
}
