package com.ezmata.messenger.model;

public class Message {
    private long id;
    private long conversationId;
    private long senderId;
    private String content;
    private long timestamp;

    public Message(long id, long conversationId, long senderId, String content, long timestamp) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
    }
}
