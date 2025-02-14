package com.example.EchoLife.controllers;
import com.example.EchoLife.entities.AuthRequest;
import com.example.EchoLife.entities.User;
import com.example.EchoLife.securityConfig.JwtUtil;
import com.example.EchoLife.services.AuthService;
import com.example.EchoLife.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {

        String token = authService.authenticate(authRequest.getEmail(), authRequest.getPassword());
        System.out.println(token);
        return ResponseEntity.ok(token);
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

            if (username == null) {
                return ResponseEntity.status(401).body("Token invalide ou expiré");
            }

            // Récupérer les données de l'utilisateur
            User user = userService.getUserByUsername(username);

            if (user == null) {
                return ResponseEntity.status(404).body("Utilisateur non trouvé");
            }

            return ResponseEntity.ok(user); // Retourne les données de l'utilisateur

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Une erreur s'est produite");
        }
    }

}
