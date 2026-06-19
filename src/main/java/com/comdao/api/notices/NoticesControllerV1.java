package com.comdao.api.notices;

import com.comdao.api.notices.entities.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notice")
public class NoticesControllerV1 {
    private final NoticesService noticesService;

    @GetMapping
    public ResponseEntity<Page<Notice>> getNotices(
            @AuthenticationPrincipal Long userId,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(noticesService.getNotifications(userId, page, size));
    }
}
