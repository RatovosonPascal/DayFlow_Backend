package com.example.EchoLife.services;

import com.example.EchoLife.entities.PasswordResetToken;
import com.example.EchoLife.entities.Role;
import com.example.EchoLife.entities.User;
import com.example.EchoLife.repositories.PasswordResetTokenRepository;
import com.example.EchoLife.repositories.UserRepository;
import com.example.EchoLife.securityConfig.JwtUtil;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JavaMailSender mailSender;
    private final PasswordResetTokenRepository tokenRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, JavaMailSender mailSender, PasswordResetTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.mailSender = mailSender;
        this.tokenRepository = tokenRepository;
    }

    // 🔹 Inscription d'un nouvel utilisateur
    public User registerUser(User user) {
        // Vérifier si l'email est déjà utilisé
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("L'email est déjà pris !");
        }

        // Encoder le mot de passe avant de sauvegarder
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assigner un rôle par défaut s'il n'est pas défini
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        return userRepository.save(user);
    }

    //  Trouver un utilisateur par son username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    //  Trouver un utilisateur par son email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    //  Trouver un utilisateur par son ID
    public Optional<User> findMe(long id) {
        return userRepository.findById(id);
    }

    //  Récupérer un utilisateur par son username avec exception si non trouvé
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    //  Trouver un utilisateur via le token
    public User findByToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7); // Supprimer le préfixe "Bearer "
            }

            String email = jwtUtil.extractUsername(token);
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        } catch (Exception e) {
            return null; // Retourne null si le token est invalide
        }
    }

    //  Sauvegarde d'un utilisateur après modification
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void processForgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.save(resetToken);

        String resetUrl = "http://localhost:8080/api/auth/reset-password?token=" + token;
        sendResetEmail(user.getEmail(), resetUrl);
    }
    private void sendResetEmail(String email, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Réinitialisation de votre mot de passe");
        message.setText("Cliquez sur le lien suivant pour réinitialiser votre mot de passe : " + resetUrl);
        mailSender.send(message);
    }
}
