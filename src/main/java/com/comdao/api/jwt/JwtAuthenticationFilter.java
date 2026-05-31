package com.comdao.api.jwt;

import com.comdao.api.user.entities.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = request.getHeader("Authorization");
        if (jwtToken == null || !jwtToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = jwtToken.substring(7);
        String username = null;
        try {
            username = jwtService.getSubject(jwtToken);
            if (username == null || username.trim().isBlank()) {
                writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Jwt Payload Invalid", "Token doesnot contain login id information");
            }
        } catch (ExpiredJwtException e) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Jwt Expired", e.getMessage());
            return;
        } catch (MalformedJwtException e) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid Jwt", e.getMessage());
            return;
        } catch (Exception e) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Something wrong", e.getMessage());
            return;
        }

        User user = null;
        try {
            user = (User) userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "User Not found", e.getMessage());
            return;
        }

        if (!user.getIsActive()) {
            writeErrorResponse(response, HttpStatus.FORBIDDEN, "User disabled", "This user is no longer active on the service");
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getId(), null, user.getAuthorities())
        );

        filterChain.doFilter(request, response);
    }

    public void writeErrorResponse(HttpServletResponse response,
                                   HttpStatus status,
                                   String message,
                                   String details) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        Map<String, String> body = new LinkedHashMap<>();
        body.put("timestamp", String.valueOf(LocalDateTime.now()));
        body.put("status", String.valueOf(status.value()));
        body.put("message", message);
        body.put("details", details);

        String jsonResponse = objectMapper.writeValueAsString(body);
        response.getWriter().write(jsonResponse);
    }
}
