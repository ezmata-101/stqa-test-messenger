package com.ezmata.messenger.api.request;

public record SignupRequest (
        String username,
        String password,
        String email
){}
