package com.ezmata.messenger.records.request;

public record CreateConversationRequest (
    String type,
    String name,
    long[] memberIds
) {
    public boolean isGroup() {
        return "GROUP".equalsIgnoreCase(type);
    }

    public long[] getMembersList() {
        return memberIds;
    }
}
