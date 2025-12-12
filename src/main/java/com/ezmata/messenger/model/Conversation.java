package com.ezmata.messenger.model;

import java.time.Instant;

public class Conversation {
    private long id;
    private ConversationType type;
    private String name;
    private Instant createdAt;
    private long createdBy;

    public Conversation(long id, ConversationType type, String name, Instant createdAt, long createdBy) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type.name();
    }

    public void setName(String name) {
        this.name = name;
    }
}
