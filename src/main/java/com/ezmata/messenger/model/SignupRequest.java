package com.ezmata.messenger.model;

public record SignupRequest (
        String username,
        String password,
        String email
){}
