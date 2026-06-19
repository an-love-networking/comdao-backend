package com.comdao.api.user;

import com.comdao.api.user.dto.*;
import com.comdao.api.user.exceptions.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserControllerV1 {
    private final UserService userService;


    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid @RequestBody UserRegistrationDto registrationInfo)
            throws UpdateInfoCollisionException {
        System.out.println(registrationInfo);
        userService.registerUser(registrationInfo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest)
            throws UserDisabledException, UserNotFoundException {
        System.out.println(loginRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.login(loginRequest));
    }


    @GetMapping("profile")
    public ResponseEntity<UserProfileResponseDto> getProfile(@AuthenticationPrincipal Long userId)
            throws UserDisabledException, UserNotFoundException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getProfile(userId));
    }


    @GetMapping("settings")
    public ResponseEntity<UserSettingsResponseDto> getSettings(@AuthenticationPrincipal Long userId)
            throws UserDisabledException, UserNotFoundException {
        return new ResponseEntity<>(userService.getSettings(userId), HttpStatus.OK);
    }


    @PutMapping("profile")
    public ResponseEntity<UserProfileResponseDto> updateProfile(
            @Valid @RequestBody UserProfileUpdateDto update,
            @AuthenticationPrincipal Long userId
    ) throws UserDisabledException, UserNotFoundException, UpdateInfoCollisionException {
        System.out.println(update);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.updateProfile(userId, update));
    }


    @PutMapping("settings")
    public ResponseEntity<UserSettingsResponseDto> updateSettings(
            @Valid @RequestBody UserSettingsUpdateDto update,
            @AuthenticationPrincipal Long userId
    ) throws UserDisabledException, UserNotFoundException {
        System.out.println(update);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.updateSettings(userId, update));
    }


    @PutMapping("password")
    public ResponseEntity<Void> updatePassword(
            @Valid @RequestBody UserPasswordUpdateDto update,
            @AuthenticationPrincipal Long userId
    ) throws PasswordPolicyViolationException, UserDisabledException,
            UserNotFoundException, PasswordUpdateViolationException {
        userService.updatePassword(userId, update);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping("disable")
    public ResponseEntity<Void> disableUser(@RequestParam(name = "confirm") Boolean isConfirmed,
                                            @AuthenticationPrincipal Long userId)
            throws UserDisabledException, UserNotFoundException {
        if (isConfirmed) {
            userService.disableUser(userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


    @PostMapping("logout")
    public ResponseEntity<Void> logoutUser(
            @RequestParam(name = "confirm") Boolean isConfirmed,
            @AuthenticationPrincipal Long userId
    ) throws UserNotFoundException, UserDisabledException {
        if (isConfirmed) {
            userService.logout(userId);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
