package com.comdao.api.user.dto;

import com.comdao.api.base.RegExPatterns;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserProfileUpdateDto {
    @Pattern(
            regexp = RegExPatterns.USERNAME,
            message = RegExPatterns.USERNAME_MESSAGE
    )
    private String username;
    @Pattern(
            regexp = RegExPatterns.EMAIL,
            message = RegExPatterns.EMAIL_MESSAGE
    )
    private String email;
    @Pattern(
            regexp = RegExPatterns.PHONE,
            message = RegExPatterns.PHONE_MESSAGE
    )
    private String phone;

    @Pattern(
            regexp = RegExPatterns.FULLNAME,
            message = RegExPatterns.FULLNAME_MESSAGE
    )
    @NotNull
    private String fullName;
    private String address;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;

    @AssertTrue(message = "Either an email or phone number must be available")
    public Boolean eitherEmailOrPhoneIsNotNullOrBlank() {
        return (email != null && !email.isBlank()) ||
                (phone != null && !phone.isBlank());
    }
}
