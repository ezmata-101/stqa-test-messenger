package com.ezmata.messenger.model;

public record LoginRequest(
        String username,
        String password
) {}
