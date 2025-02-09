package com.example.EchoLife.controllers;
import com.example.EchoLife.entities.AuthRequest;
import com.example.EchoLife.entities.User;
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

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {

        String token = authService.authenticate(authRequest.getEmail(), authRequest.getPassword());
        return ResponseEntity.ok(token);
    }
    @GetMapping("/me/{id}")
    public Optional<User> getMe(@PathVariable long id) {
        return userService.findMe(id);
    }
}
