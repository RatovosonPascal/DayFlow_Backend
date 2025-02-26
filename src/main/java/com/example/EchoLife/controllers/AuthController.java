package com.example.EchoLife.controllers;
import com.example.EchoLife.entities.AuthRequest;
import com.example.EchoLife.entities.ForgotPasswordRequest;
import com.example.EchoLife.entities.User;
import com.example.EchoLife.securityConfig.JwtUtil;
import com.example.EchoLife.services.AuthService;
import com.example.EchoLife.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, UserService userService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User newUser = userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
    @PutMapping("/user/update")
    public ResponseEntity<User> updateUser(@RequestBody User updatedUser, @RequestHeader("Authorization") String token) {
        // Récupérer l'utilisateur à partir du token
        User currentUser = userService.findByToken(token);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Mettre à jour les informations
        currentUser.setUsername(updatedUser.getUsername());
        currentUser.setEmail(updatedUser.getEmail());

        // Sauvegarde dans la base de données
        userService.saveUser(currentUser);

        return ResponseEntity.ok(currentUser);
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest authRequest) {

        // Générer le token en fonction des informations d'authentification
        String token = authService.authenticate(authRequest.getEmail(), authRequest.getPassword());

        if (token == null) {
            // Si le token est nul, on renvoie une erreur d'authentification
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // Créer un objet JSON pour contenir le token
        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        // Renvoyer la réponse avec un statut 200 OK et l'objet JSON contenant le token
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/{id}")
    public Optional<User> getMe(@PathVariable long id) {
        return userService.findMe(id);
    }
    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Check if the Authorization header is properly formatted
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(400).body("Invalid Authorization header format");
            }

            // Extraction du token de l'en-tête Authorization (Format: "Bearer <token>")
            String token = authorizationHeader.substring(7); // Supprime "Bearer " du début
            System.out.println("Token reçu : " + token);

            // Extraire le nom d'utilisateur du token
            String username = jwtUtil.extractUsername(token);
            System.out.println("Username extrait : " + username);

            if (username == null) {
                return ResponseEntity.status(401).body("Token invalide ou expiré");
            }

            // Récupérer les données de l'utilisateur
            Optional<User> user = userService.findByEmail(username);

            if (user == null) {
                return ResponseEntity.status(404).body("Utilisateur non trouvé");
            }

            return ResponseEntity.ok(user); // Retourne les données de l'utilisateur

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Une erreur s'est produite");
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.processForgotPassword(request.getEmail());
        return ResponseEntity.ok("Un e-mail de réinitialisation a été envoyé si l'adresse existe.");
    }

}
