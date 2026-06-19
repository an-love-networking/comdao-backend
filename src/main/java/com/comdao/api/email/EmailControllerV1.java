package com.comdao.api.email;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email")
public class EmailControllerV1 {
    @PostMapping
    public ResponseEntity<Void> verifyEmail(@RequestParam(name = "id") String id) {
        // TODO
        return null;
    }
}
