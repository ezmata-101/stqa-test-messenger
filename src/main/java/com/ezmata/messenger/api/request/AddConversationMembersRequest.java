package com.ezmata.messenger.api.request;

public record AddConversationMembersRequest(
        long conversationId,
        long[] newMembers
) {}
