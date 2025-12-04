package com.ezmata.messenger.api.request;

public record LoginRequest(
        String username,
        String password
) {}
