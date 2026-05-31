package com.comdao.api.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordUpdateDto {
    @Size(min = 8, message = "Password should be longer than 8 characters")
    @NotBlank
    String oldPassword;
    @Size(min = 8, message = "Password should be longer than 8 characters")
    @NotBlank
    String newPassword;
}
