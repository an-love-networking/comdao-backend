package com.comdao.api.websocket;

import com.comdao.api.jwt.JwtService;
import com.comdao.api.user.entities.User;
import com.comdao.api.user.entities.enums.Role;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        // ── CONNECT: validate JWT and set principal on session ───────────────
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String jwtToken = extractJwtFromHeaders(accessor);
            if (jwtToken == null) {
                log.warn("CONNECT rejected — missing Authorization header");
                return null;
            }

            String username;
            try {
                username = jwtService.getSubject(jwtToken);
            } catch (ExpiredJwtException e) {
                log.warn("CONNECT rejected — expired JWT: {}", e.getMessage());
                return null;
            } catch (MalformedJwtException e) {
                log.warn("CONNECT rejected — malformed JWT: {}", e.getMessage());
                return null;
            } catch (Exception e) {
                log.warn("CONNECT rejected — JWT error: {}", e.getMessage());
                return null;
            }

            if (username == null || username.isBlank()) {
                log.warn("CONNECT rejected — JWT has no subject");
                return null;
            }

            User user;
            try {
                user = (User) userDetailsService.loadUserByUsername(username);
            } catch (UsernameNotFoundException e) {
                log.warn("CONNECT rejected — user not found: {}", username);
                return null;
            }

            if (!user.getIsActive()) {
                log.warn("CONNECT rejected — user is disabled: {}", username);
                return null;
            }

            if (!jwtToken.equals(user.getActiveJwtToken())) {
                log.warn("CONNECT rejected — token is not the active token for user: {}", username);
                return null;
            }

            // store full User object as principal — available on all subsequent frames
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
            );
            accessor.setUser(auth);
            log.info("CONNECT authorized — user: {} session: {}", username, accessor.getSessionId());
        }

        // ── DISCONNECT: log and clean up if needed ───────────────────────────
        else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            Authentication auth = (Authentication) accessor.getUser();
            if (auth != null) {
                User user = (User) auth.getPrincipal();
                log.info("DISCONNECT — user: {} session: {}", user.getUsername(), accessor.getSessionId());
                // add any cleanup logic here, e.g. update online status
            } else {
                log.info("DISCONNECT — unauthenticated session: {}", accessor.getSessionId());
            }
        }

        // ── SUBSCRIBE: verify ownership for private queues ───────────────────
        else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            if (destination != null && destination.contains("/queue/private")) {
                Authentication auth = (Authentication) accessor.getUser();
                if (auth == null) {
                    log.warn("SUBSCRIBE rejected — no principal on session for destination: {}", destination);
                    return null;
                }
                User user = (User) auth.getPrincipal();
                String usernameFromDestination = extractUsernameFromDestination(destination);
                if ((usernameFromDestination != null && !usernameFromDestination.equals("queue") && !usernameFromDestination.equals(user.getUsername())) || user.getRole().equals(Role.ADMIN)) {
                    log.warn("SUBSCRIBE rejected — user {} tried to subscribe to {}", user.getUsername(), destination);
                    return null;
                }
            }
        }

        return message;
    }

    private String extractJwtFromHeaders(StompHeaderAccessor accessor) {
        List<String> authHeaders = accessor.getNativeHeader("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String bearer = authHeaders.get(0);
            if (bearer != null && bearer.startsWith("Bearer ")) {
                return bearer.substring(7);
            }
        }
        return null;
    }

    private String extractUsernameFromDestination(String destination) {
        // /user/{username}/queue/private
        if (destination.startsWith("/user/")) {
            String after = destination.substring("/user/".length());
            int slash = after.indexOf('/');
            return slash > 0 ? after.substring(0, slash) : after;
        }
        return null;
    }
}