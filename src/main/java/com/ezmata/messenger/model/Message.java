package com.ezmata.messenger.model;

public class Message {
    private final long id;
    private final long conversationId;
    private final long senderId;
    private final String content;
    private final long timestamp;

    public Message(long id, long conversationId, long senderId, String content, long timestamp) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", conversationId=" + conversationId +
                ", senderId=" + senderId +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public long getId() {
        return id;
    }
    public long getConversationId() {
        return conversationId;
    }
    public long getSenderId() {
        return senderId;
    }
    public String getContent() {
        return content;
    }
    public long getTimestamp() {
        return timestamp;
    }
}
