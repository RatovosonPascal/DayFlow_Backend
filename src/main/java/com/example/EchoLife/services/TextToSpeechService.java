package com.example.EchoLife.services;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.*;
@Service
public class TextToSpeechService {

    // Méthode pour convertir du texte en audio
    public void convertTextToAudio(String text) throws Exception {
        // Instancier le client Text-to-Speech
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {

            // Configurer la demande
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            // Sélectionner la voix et la langue
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("fr-FR") // Langue française
                    .setName("fr-FR-Wavenet-D") // Voix spécifique (peut être changée)
                    .build();

            // Paramètres de sortie (MP3)
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3) // Choix du format audio
                    .build();

            // Effectuer la synthèse vocale
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Récupérer le résultat audio
            ByteString audioContents = response.getAudioContent();

            // Sauvegarder l'audio dans un fichier ou le jouer directement
            try (OutputStream out = new FileOutputStream("output.mp3")) {
                out.write(audioContents.toByteArray());
            }

            // Jouer l'audio (si besoin)
            playAudio("output.mp3");
        }
    }

    // Méthode pour jouer l'audio avec le système
    private void playAudio(String filename) throws Exception {
        File audioFile = new File(filename);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.start();

        // Attendre la fin de l'audio
        Thread.sleep(clip.getMicrosecondLength() / 1000);
    }
}
