package com.library.model.bo;

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
}
