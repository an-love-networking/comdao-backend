package com.comdao.api.user.dto;

import com.comdao.api.base.RegExPatterns;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRegistrationDto {
    @NotNull
    @Pattern(
            regexp = RegExPatterns.FULLNAME,
            message = RegExPatterns.FULLNAME_MESSAGE
    )
    String fullName;
    @Pattern(
            regexp = RegExPatterns.USERNAME,
            message = RegExPatterns.USERNAME_MESSAGE
    )
    String username;
    @Pattern(
            regexp = RegExPatterns.PHONE,
            message = RegExPatterns.PHONE_MESSAGE
    )
    String phone;
    @Pattern(
            regexp = RegExPatterns.EMAIL,
            message = RegExPatterns.EMAIL_MESSAGE
    )
    String email;
    @NotNull
    @Size(min = 8, message = "Password should be longer than 8 characters")
    String password;

    @AssertTrue(message = "Please provide either an email or a phone number")
    public Boolean hasEmailOrPhone() {
        return (email != null && !email.isBlank()) && (phone != null && !phone.isBlank());
    }
}
