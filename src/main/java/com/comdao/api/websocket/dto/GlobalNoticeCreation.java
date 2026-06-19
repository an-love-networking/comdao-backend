package com.comdao.api.websocket.dto;

import com.comdao.api.notices.entities.enums.Type;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GlobalNoticeCreation {
    private Type type;
    @NotBlank
    private String title;
    @NotBlank
    private String summary;
    @NotBlank
    private String content;
}
