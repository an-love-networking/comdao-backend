package com.comdao.api.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler, AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("utf-8");


        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", new Date());
        body.put("status", HttpStatus.FORBIDDEN);
        body.put("error", "Forbidden");
        body.put("message", "Only admins allowed");
        body.put("path", request.getRequestURI());

        response.getOutputStream().println(objectMapper.writeValueAsString(body));
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("utf-8");


        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", new Date());
        body.put("status", HttpStatus.FORBIDDEN);
        body.put("error", "Forbidden");
        body.put("message", "No JWT in header");
        body.put("path", request.getRequestURI());

        response.getOutputStream().println(objectMapper.writeValueAsString(body));
    }
}
