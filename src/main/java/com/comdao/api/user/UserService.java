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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    @Value("${app.security.password-change-interval-days}")
    private Long passwordChangeIntervalInDays;

    @Transactional(readOnly = true)
    public UserProfileResponseDto getProfile(Long id)
            throws UserNotExistException, UserDisabledException {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotExistException("User is not registered")
        );

        if (!user.getIsActive()) {
            throw new UserDisabledException("User is disabled");
        }

        return mapper.toUserProfileResponse(user);
    }

    @Transactional(readOnly = true)
    public UserSettingsResponseDto getSettings(Long id)
            throws UserDisabledException, UserNotExistException {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotExistException("User is not registered")
        );

        if (!user.getIsActive()) {
            throw new UserDisabledException("User is disabled");
        }


        UserSettings settings = userSettingsRepository.findById(id).orElseThrow(
                () -> new UserNotExistException("User is not registered")
        );
        UserSettingsResponseDto userSettingsResponse = mapper.toUserSettingsResponse(settings);
        return userSettingsResponse;
    }

    @Transactional(readOnly = true)
    private Boolean checkAvailable(String email, String phone, String username, Long id)
            throws UpdateInfoCollisionException {
        HashMap<String, String> details = new HashMap<>();

        if (email != null &&
                userRepository.existsByEmailAndIdIsNot(email, id))
            details.put("email", "Email has already been taken");

        if (phone != null &&
                userRepository.existsByPhoneAndIdIsNot(phone, id))
            details.put("phone", "Phone number has already been taken");

        if (username != null &&
                userRepository.existsByUsernameAndIdIsNot(username, id))
            details.put("username", "Username has already been taken");

        if (details.isEmpty()) return true;
        else throw new UpdateInfoCollisionException("Info collision", details);
    }

    @Transactional
    public UserProfileResponseDto updateProfile(Long id, UserProfileUpdateDto update)
            throws UserNotExistException, UserDisabledException, UpdateInfoCollisionException, EmailPhoneRemovalException {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotExistException("User is not registered to be disabled")
        );

        if (!user.getIsActive()) {
            throw new UserDisabledException("User is disabled already");
        }

        checkAvailable(update.getEmail(),
                update.getPhone(),
                update.getUsername(),
                user.getId());

//        if (update.getAddress() == null) user.setAddress(null);

//        if (update.getPhone() == null && update.getEmail() == null)
//            throw new EmailPhoneRemovalException("Cannot remove both");

        if (update.getPhone() == null || update.getPhone().isBlank()) user.setPhone(null);
        if (update.getEmail() == null || update.getEmail().isBlank()) user.setEmail(null);

        mapper.updateUserProfile(update, user);
        return mapper.toUserProfileResponse(userRepository.save(user));
    }

    public UserSettingsResponseDto updateSettings(Long id, UserSettingsUpdateDto update) throws UserNotExistException, UserDisabledException {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotExistException("User is not registered to be disabled")
        );

        if (!user.getIsActive()) {
            throw new UserDisabledException("User is disabled already");
        }

        UserSettings settings = userSettingsRepository.findById(id).orElseThrow(
                () -> new UserNotExistException("User is not registered")
        );
        mapper.updateUserSettings(update, settings);
        return mapper.toUserSettingsResponse(userSettingsRepository.save(settings));
    }

    @Transactional
    public void updatePassword(Long id, UserPasswordUpdateDto passwordUpdate)
            throws UserNotExistException, UserDisabledException,
            PasswordPolicyViolationException, PasswordUpdateViolationException {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotExistException("User is not registered to be disabled")
        );

        if (!user.getIsActive()) {
            throw new UserDisabledException("User is disabled already");
        }

        if (!passwordEncoder.matches(passwordUpdate.getOldPassword(), user.getPassword()))
            throw new PasswordUpdateViolationException("Old password incorrect");

        if (user.getLastChangePassword() != null &&
                user.getLastChangePassword().minusDays(passwordChangeIntervalInDays).isAfter(user.getLastChangePassword())) {
            throw new PasswordPolicyViolationException("You cannot change password now");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdate.getNewPassword()));

        user.setLastChangePassword(LocalDateTime.now());
    }

    @Transactional
    public void disableUser(Long id) throws UserNotExistException, UserDisabledException {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotExistException("User is not registered to be disabled")
        );

        if (!user.getIsActive()) {
            throw new UserDisabledException("User is disabled already");
        }

        user.setIsActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void registerUser(UserRegistrationDto registerInfo) throws UpdateInfoCollisionException {
        checkAvailable(registerInfo.getEmail(),
                registerInfo.getPhone(),
                registerInfo.getUsername(),
                null);
        User user = mapper.fillRegistration(registerInfo);
        user.setPassword(passwordEncoder.encode(registerInfo.getPassword()));
        user = userRepository.save(user);
        UserSettings settings = new UserSettings();
        settings.setUser(user);
        userSettingsRepository.save(settings);
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto request) throws UserDisabledException, UserNotExistException {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword())
        );
        User user = userRepository.findByLoginId(request.getLoginId()).orElseThrow(
                () -> new UserNotExistException("User is unregistered"));
        if (!user.getIsActive())
            throw new UserDisabledException("User is disabled");
        String jwt = jwtService.generateToken(user.getUsername());
        return new LoginResponseDto(jwt);
    }
}
