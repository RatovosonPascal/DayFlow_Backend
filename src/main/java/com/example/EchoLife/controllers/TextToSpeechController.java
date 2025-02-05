package com.example.EchoLife.controllers;

import com.example.EchoLife.services.TextToSpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audio")
public class TextToSpeechController {

    // Injection de la dépendance TextToSpeechService
    private final TextToSpeechService textToSpeechService;

    @Autowired
    public TextToSpeechController(TextToSpeechService textToSpeechService) {
        this.textToSpeechService = textToSpeechService;
    }

    /**
     * Endpoint pour tester la synthèse vocale.
     * @param text Texte à convertir en audio
     * @return Message de succès ou d'échec
     */
    @GetMapping("/synthesize")
    public String synthesizeText(@RequestParam String text) {
        try {
            // Appeler la méthode de conversion texte -> audio
            textToSpeechService.convertTextToAudio(text);
            return "Synthèse vocale terminée avec succès ! Le texte a été converti en audio.";
        } catch (Exception e) {
            // En cas d'erreur
            return "Erreur lors de la synthèse vocale : " + e.getMessage();
        }
    }
}

