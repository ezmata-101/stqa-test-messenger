package com.ezmata.messenger.api.response;

public record LoginResponse(
        String token,
        long userId
) {}
