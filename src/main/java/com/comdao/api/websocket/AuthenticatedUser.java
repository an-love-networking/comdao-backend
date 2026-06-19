package com.comdao.api.websocket;

import com.comdao.api.user.entities.enums.Role;

import java.security.Principal;

public record AuthenticatedUser(String username, Role role) implements Principal {
    @Override
    public String getName() {
        return username;
    }
}