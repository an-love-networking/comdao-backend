package com.comdao.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsResponseDto {
    Boolean shouldNotify;
    Boolean useSms;
    Boolean includePromotion;
    Boolean useDarkMode;
    Boolean useTwoStepVerification;
}
