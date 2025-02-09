package com.example.EchoLife.controllers;

import com.example.EchoLife.entities.ResponseMessage;
import com.example.EchoLife.entities.UserMessageDTO;
import com.example.EchoLife.services.ChatService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    public ChatController(ChatService chatService, ObjectMapper objectMapper) {
        this.chatService = chatService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody UserMessageDTO userMessageDTO) {
        try {
            // 🔹 Appel au service pour obtenir la réponse de l'API Mistral
            String chatbotResponse = chatService.getResponseFromMistral(userMessageDTO.getContent());

            // 🔹 Désérialisation de la réponse JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseNode = objectMapper.readTree(chatbotResponse);

            // 🔹 Extraction du message du chatbot
            JsonNode choices = responseNode.path("choices");
            String chatbotMessage = choices.get(0).path("message").path("content").asText();

            // 🔹 Conversion en JsonNode
            JsonNode chatbotMessageNode = objectMapper.createObjectNode().put("response", chatbotMessage);

            // 🔹 Retourner uniquement la réponse utile du chatbot
            return ResponseEntity.ok(new ResponseMessage("success", chatbotMessageNode));
        } catch (Exception e) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode errorMessage = objectMapper.createObjectNode().put("error", "Erreur lors de la communication avec l'API");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("error", errorMessage));
        }
    }
}
