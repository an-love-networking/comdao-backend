package com.comdao.api.user;

import com.comdao.api.user.dto.*;
import com.comdao.api.user.exceptions.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserControllerV1 {
    private final UserService userService;

    @PostMapping("register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody UserRegistrationDto registrationInfo
    ) throws UpdateInfoCollisionException {
        System.out.println(registrationInfo);
        userService.registerUser(registrationInfo);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) throws UserDisabledException, UserNotExistException {
        System.out.println(request);
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    @GetMapping("profile")
    public ResponseEntity<UserProfileResponseDto> getProfile(/*@RequestParam(name = "id")*/ @AuthenticationPrincipal Long id)
            throws UserDisabledException, UserNotExistException {
        return new ResponseEntity<>(userService.getProfile(id), HttpStatus.OK);
    }

    @GetMapping("settings")
    public ResponseEntity<UserSettingsResponseDto> getSettings(@AuthenticationPrincipal/*@RequestParam(name = "id")*/ Long id)
            throws UserDisabledException, UserNotExistException {
        return new ResponseEntity<>(userService.getSettings(id), HttpStatus.OK);
    }

    @PutMapping("profile")
    public ResponseEntity<UserProfileResponseDto> updateProfile(
            @Valid @RequestBody UserProfileUpdateDto update,
            @AuthenticationPrincipal/*@RequestParam(name = "id")*/ Long id
    ) throws UserDisabledException, UserNotExistException, UpdateInfoCollisionException, EmailPhoneRemovalException {
        return new ResponseEntity<>(userService.updateProfile(id, update), HttpStatus.OK);
    }

    @PutMapping("settings")
    public ResponseEntity<UserSettingsResponseDto> updateSettings(
            @Valid @RequestBody UserSettingsUpdateDto update,
            @AuthenticationPrincipal/*@RequestParam(name = "id")*/ Long id
    ) throws UserDisabledException, UserNotExistException {
        return new ResponseEntity<>(userService.updateSettings(id, update), HttpStatus.OK);
    }

    @PutMapping("password")
    public ResponseEntity<Void> updatePassword(
            @Valid @RequestBody UserPasswordUpdateDto update,
            @AuthenticationPrincipal/*@RequestParam(name = "id")*/ Long id
    ) throws PasswordPolicyViolationException, UserDisabledException, UserNotExistException {
        userService.updatePassword(id, update);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("disable")
    public ResponseEntity<Void> disableUser(@RequestParam(name = "confirm") Boolean confirmed,
                                            @AuthenticationPrincipal/*@RequestParam(name = "id")*/ Long id)
            throws UserDisabledException, UserNotExistException {
        if (confirmed) {
            userService.disableUser(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
