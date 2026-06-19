package com.comdao.api.user;

import com.comdao.api.jwt.JwtService;
import com.comdao.api.user.dto.*;
import com.comdao.api.user.entities.User;
import com.comdao.api.user.entities.UserSettings;
import com.comdao.api.user.exceptions.*;
import com.comdao.api.user.repositories.UserRepository;
import com.comdao.api.user.repositories.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final UserChecker userChecker;

    @Value("${app.security.password-change-interval-days}")
    private Long passwordChangeIntervalInDays;


    @Transactional(readOnly = true)
    public UserProfileResponseDto getProfile(Long userId)
            throws UserNotFoundException, UserDisabledException {
        User user = userChecker.checkExistAndActiveById(userId);
        return mapper.toUserProfileResponse(user);
    }


    @Transactional(readOnly = true)
    public UserSettingsResponseDto getSettings(Long userId)
            throws UserDisabledException, UserNotFoundException {
        User user = userChecker.checkExistAndActiveById(userId);

        UserSettings settings = userSettingsRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User is not registered")
        );

        UserSettingsResponseDto userSettingsResponse = mapper.toUserSettingsResponse(settings);
        return userSettingsResponse;
    }


    @Transactional
    public UserProfileResponseDto updateProfile(Long userId, UserProfileUpdateDto updatedProfile)
            throws UserNotFoundException, UserDisabledException, UpdateInfoCollisionException {
        User user = userChecker.checkExistAndActiveById(userId);

        userChecker.checkEmailPhoneUsernameAvailable(
                updatedProfile.getEmail(),
                updatedProfile.getPhone(),
                updatedProfile.getUsername(),
                user.getId());

        if (updatedProfile.getPhone() == null || updatedProfile.getPhone().isBlank())
            user.setPhone(null);
        if (updatedProfile.getEmail() == null || updatedProfile.getEmail().isBlank())
            user.setEmail(null);

        mapper.updateUserProfile(updatedProfile, user);
        return mapper.toUserProfileResponse(userRepository.save(user));
    }


    @Transactional
    public UserSettingsResponseDto updateSettings(Long userId, UserSettingsUpdateDto updatedSettings)
            throws UserNotFoundException, UserDisabledException {
        User user = userChecker.checkExistAndActiveById(userId);

        UserSettings settings = userSettingsRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User is not registered")
        );

        mapper.updateUserSettings(updatedSettings, settings);
        return mapper.toUserSettingsResponse(userSettingsRepository.save(settings));
    }


    @Transactional
    public void updatePassword(Long userId, UserPasswordUpdateDto passwordUpdate)
            throws UserNotFoundException, UserDisabledException,
            PasswordPolicyViolationException, PasswordUpdateViolationException {
        User user = userChecker.checkExistAndActiveById(userId);

        if (!passwordEncoder.matches(passwordUpdate.getOldPassword(), user.getPassword()))
            throw new PasswordUpdateViolationException("Old password incorrect");

        if (!passwordEncoder.matches(passwordUpdate.getNewPassword(), user.getPassword()))
            throw new PasswordUpdateViolationException("New password is the same as the old password");

        if (user.getLastChangePassword() != null &&
                user.getLastChangePassword().minusDays(passwordChangeIntervalInDays).isAfter(user.getLastChangePassword())) {
            throw new PasswordPolicyViolationException("You cannot change password now");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdate.getNewPassword()));
        user.setLastChangePassword(LocalDateTime.now());
    }


    @Transactional
    public void disableUser(Long userId) throws UserNotFoundException, UserDisabledException {
        User user = userChecker.checkExistAndActiveById(userId);

        user.setIsActive(false);
        userRepository.save(user);
    }


    @Transactional
    public void registerUser(UserRegistrationDto registerInfo) throws UpdateInfoCollisionException {
        userChecker.checkEmailPhoneUsernameAvailable(
                registerInfo.getEmail(),
                registerInfo.getPhone(),
                registerInfo.getUsername(),
                null
        );

        User user = mapper.fillRegistration(registerInfo);
        user.setPassword(passwordEncoder.encode(registerInfo.getPassword()));
        user = userRepository.save(user);

        UserSettings settings = new UserSettings();
        settings.setUser(user);

        userSettingsRepository.save(settings);
    }


    @Transactional
    public LoginResponseDto login(LoginRequestDto request) throws UsernameNotFoundException {
        Authentication auth = null;
        try {
            auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword())
            );
        } catch (Exception e) {
            throw new UsernameNotFoundException("User is not registered");
        }
//
        User user = (User) auth.getPrincipal();
        System.out.println(user);
        String jwt = jwtService.generateToken(user.getUsername());
        user.setActiveJwtToken(jwt);

        user = userRepository.save(user);

        return new LoginResponseDto(jwt);
    }

    @Transactional
    public void logout(Long userId) throws UserNotFoundException, UserDisabledException {
        User user = userChecker.checkExistAndActiveById(userId);
        user.setActiveJwtToken(null);
        userRepository.save(user);
    }
}
