package com.ezmata.messenger.model;

public class Conversation {
    private long id;
    private ConversationType type;
    private String name;
    private long createdAt;
    private long createdBy;

    public Conversation(long id, ConversationType type, String name, long createdAt, long createdBy) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }
}
