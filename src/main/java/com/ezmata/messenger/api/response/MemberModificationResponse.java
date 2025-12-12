package com.ezmata.messenger.api.response;

public record MemberModificationResponse(
        String message,
        long conversationId,
        long[] memberIds
) {}
