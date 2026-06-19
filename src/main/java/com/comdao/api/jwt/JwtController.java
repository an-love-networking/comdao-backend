package com.comdao.api.jwt;

import com.comdao.api.user.UserChecker;
import com.comdao.api.user.exceptions.UserDisabledException;
import com.comdao.api.user.exceptions.UserNotFoundException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/token")
@CrossOrigin(origins = "*")
public class JwtController {
    private final JwtService jwtService;
    private final UserChecker userChecker;

    @PostMapping
    public ResponseEntity<Boolean> verifyToken(
            @NotBlank @RequestBody String jwtToken
    )
            throws UserNotFoundException, UserDisabledException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(jwtService.isValid(jwtToken));
    }
}
