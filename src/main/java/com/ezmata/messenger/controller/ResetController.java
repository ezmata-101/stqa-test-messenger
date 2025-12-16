package com.ezmata.messenger.controller;

import com.ezmata.messenger.repository.ConversationRepository;
import com.ezmata.messenger.repository.MessageRepository;
import com.ezmata.messenger.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ResetController {
    UserRepository userRepository;
    ConversationRepository conversationRepository;
    MessageRepository messageRepository;

    public ResetController(UserRepository userRepository, ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @GetMapping("/reset")
    public ResponseEntity<?> reset() {
        userRepository.reset();
        conversationRepository.reset();
        messageRepository.reset();
        return ResponseEntity.ok("Reset successful");
    }
}
