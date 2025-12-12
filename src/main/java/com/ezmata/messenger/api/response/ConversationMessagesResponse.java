package com.ezmata.messenger.api.response;

import com.ezmata.messenger.model.Message;

public record ConversationMessagesResponse(
        String message,
        long conversationId,
        Message[] messages
) {}
