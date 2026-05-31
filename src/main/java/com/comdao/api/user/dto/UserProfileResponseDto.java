package com.comdao.api.user.dto;

import com.comdao.api.user.entities.enums.Tier;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {
    private String fullName;
    private String email;
    private String phone;
    private String username;

    private String address;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate created;
    private Tier tier;
}
