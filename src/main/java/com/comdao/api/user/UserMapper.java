package com.comdao.api.user;

import com.comdao.api.user.dto.*;
import com.comdao.api.user.entities.User;
import com.comdao.api.user.entities.UserSettings;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    public UserProfileResponseDto toUserProfileResponse(User user);

    public UserSettingsResponseDto toUserSettingsResponse(UserSettings settings);

    public User fillRegistration(UserRegistrationDto registrationInfo);


    public void updateUserProfile(UserProfileUpdateDto update, @MappingTarget User user);

    public void updateUserSettings(UserSettingsUpdateDto update, @MappingTarget UserSettings settings);
}
