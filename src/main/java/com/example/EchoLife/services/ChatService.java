package com.example.EchoLife.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

   // private static final String API_URL = "https://api.mistral.ai/v1/chat/completions";
    //private static final String API_KEY = "6GCjm4b3QIDkdUXetPjc0Hwf6RLxTrta"; //clé API
    @Value("${gpt4free.api.key}")  // Injection de la clé API depuis le fichier de configuration
    private String apiKey;
    @Value("${gpt4free.api.url}")  // Injection de la clé API depuis le fichier de configuration
    private String apiURL;
    public void printApiDetails() {
        System.out.println("API Key: " + apiKey);
        System.out.println("API URL: " + apiURL);
    }

    public String getResponseFromMistral(String inputText) {
        RestTemplate restTemplate = new RestTemplate();

        //  headers avec Content-Type et Authorization
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // corps de la requête sous forme d'objet JSON
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "mistral-large-latest");

        //  Liste des messages envoyés à l'API
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", inputText);
        messages.add(userMessage);

        requestBody.put("messages", messages);

        //  Conversion en JSON
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            //  Envoi de la requête et récupération de la réponse
            ResponseEntity<String> response = restTemplate.exchange(apiURL, HttpMethod.POST, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            System.err.println("Erreur HTTP : " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return "Erreur lors de l'appel à Mistral API.";
        }
    }
}