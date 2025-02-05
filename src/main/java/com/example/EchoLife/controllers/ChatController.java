package com.example.EchoLife.controllers;
import com.example.EchoLife.entities.ResponseMessage;
import com.example.EchoLife.services.ChatService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody String userMessage) {
        ObjectMapper objectMapper = null;
        try {
            // Appel au service pour obtenir la réponse du chatbot
            String chatbotResponse = chatService.getResponseFromHuggingFace(userMessage);

            // Désérialiser le message (si c'est nécessaire, ici c'est un tableau JSON)
            objectMapper = new ObjectMapper();
            JsonNode responseNode = objectMapper.readTree(chatbotResponse);

            // Retourner la réponse du chatbot dans un format structuré
            return ResponseEntity.ok(new ResponseMessage("success", responseNode));
        } catch (Exception e) {
            JsonNode errorMessage = objectMapper.createObjectNode().put("error", "Erreur lors de la communication avec l'API");
            // En cas d'erreur, retourner un message d'erreur dans un format structuré
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("error", errorMessage));
        }
    }

}
