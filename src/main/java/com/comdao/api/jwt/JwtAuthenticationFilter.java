package com.comdao.api.jwt;

import com.comdao.api.user.entities.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String path = request.getServletPath();
//        String method = request.getMethod();
//
//        // Skip JWT validation for your public POST webhooks & auth endpoints
//        if ("POST".equalsIgnoreCase(method)) {
//            return path.equals("/api/v1/payment/webhook")
//                    || path.equals("/api/v1/user/register")
//                    || path.equals("/api/v1/user/login")
//                    || path.equals("/api/v1/token");
//        }
//
//        // Skip JWT validation for public GET endpoints
//        if ("GET".equalsIgnoreCase(method)) {
//            return path.startsWith("/api/v1/product/view")
//                    || path.startsWith("/api/v1/category/view");
//        }
//
//        return false;
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // take the jwt token out of Authorization
        String jwtToken = request.getHeader("Authorization");
        if (jwtToken == null || !jwtToken.startsWith("Bearer ")) {
            log.info("No JWT detected");
            filterChain.doFilter(request, response);
            return;
        }

        // verify the token
        jwtToken = jwtToken.substring(7);
        String username = null;
        try {
            username = jwtService.getSubject(jwtToken);
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
        if (username == null || username.trim().isBlank()) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Jwt Payload Invalid",
                    "Token does not contain login id information");
            return;
        }

        // retrieve the user
        User user = null;
        try {
            user = (User) userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "User Not found", e.getMessage());
            return;
        }

        if (!user.getIsActive()) {
            writeErrorResponse(response, HttpStatus.FORBIDDEN, "User disabled",
                    "This user is no longer active on the service");
            return;
        }

        if (!jwtToken.equals(user.getActiveJwtToken())) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid Jwt Token",
                    "You can not use this Jwt Token");
            return;
        }

        // set context with user's id
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getId(), null, user.getAuthorities())
        );

        log.info("Finish JWT auth");
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
