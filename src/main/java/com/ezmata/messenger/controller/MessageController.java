package com.ezmata.messenger.controller;

import com.ezmata.messenger.records.response.GenericResponse;
import com.ezmata.messenger.model.Message;
import com.ezmata.messenger.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    //    this will by default return last 20 messages, if params are added for pagination, it can return accordingly
//    add appropriate parameters
    @GetMapping("/messages/{conversationId}/get")
    public ResponseEntity<?> getMessages(Authentication authentication,
                                      @PathVariable String conversationId,
                                      @RequestParam(required = false, defaultValue = "0") int page,
                                      @RequestParam(required = false, defaultValue = "20") int size) {
        String username = authentication.getName();
        try{
            List<Message> messages = messageService.getMessagesForConversation(username, Long.parseLong(conversationId));
            return ResponseEntity.ok(
                    new GenericResponse<List<Message>>(
                            "Messages retrieved successfully",
                            messages
                    )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new GenericResponse<>(
                            e.getMessage(),
                            null
                    )
            );
        }
    }


    @PostMapping("/messages/{conversationId}/send")
    public ResponseEntity<?> sendMessage(Authentication authentication,
                                            @PathVariable long conversationId,
                                            @RequestBody String content) {
        String username = authentication.getName();
        try{
            System.out.println("Sending message to conversation ID: " + conversationId + " with content: " + content);
//            Message[] messages = List.toArray(messageService.sendMessageToConversation(username, conversationId, content));

            Message[] messages = messageService.sendMessageToConversation(username, conversationId, content)
                    .toArray(new Message[0]);

            return ResponseEntity.ok(
                    new GenericResponse<>(
                            "Message sent successfully",
                            messages
                    )
            );
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new GenericResponse<>(
                            e.getMessage(),
                            null
                    )
            );
        }
    }
}
