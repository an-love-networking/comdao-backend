package com.comdao.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserSettingsUpdateDto {
    Boolean shouldNotify;
    Boolean useSms;
    Boolean includePromotion;
    Boolean useDarkMode;
    Boolean useTwoStepVerification;
}
