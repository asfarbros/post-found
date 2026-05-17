package com.example.backend.controller;

import com.example.backend.config.JwtUtil;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException; // Import specific exception
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Allow your Vue app
public class AuthController {

    // DTOs (Data Transfer Objects) - defined inside the controller class
    record RegisterRequest(String name, String email, String password) {}
    record LoginRequest(String email, String password) {}
    record AuthResponse(String token) {}

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil; // Your JWT utility class

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Email is already taken!");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        // Hash the password using Bcrypt
        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        System.out.println("--- AuthController: Login attempt for email: " + request.email()); // DEBUG
        try {
            // 1. Let Spring Security try to authenticate
            System.out.println("--- AuthController: Calling authenticationManager.authenticate..."); // DEBUG
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            System.out.println("--- AuthController: Authentication successful!"); // DEBUG

            // 2. If successful, get the user details (Principal should be email)
            String email = authentication.getName();
            System.out.println("--- AuthController: Authenticated email: " + email); // DEBUG
            System.out.println("--- AuthController: Attempting to find user in repository again..."); // DEBUG
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        // This specific error should ideally not happen if authentication succeeded
                         System.err.println("--- AuthController: CRITICAL ERROR - User found during auth but not found immediately after for email: " + email); // DEBUG
                         return new UsernameNotFoundException("User data inconsistency after authentication"); 
                    });
            System.out.println("--- AuthController: Found user object for token generation: ID " + user.getId()); // DEBUG

            // 3. Generate a JWT token
             System.out.println("--- AuthController: Attempting to generate JWT token..."); // DEBUG
            String token = jwtUtil.generateToken(user);
             System.out.println("--- AuthController: JWT token generated successfully."); // DEBUG


            // 4. Send the token back to the client
             System.out.println("--- AuthController: Sending token response."); // DEBUG
            return ResponseEntity.ok(new AuthResponse(token));

        } catch (BadCredentialsException e) { // Catch specific exception for wrong password
             System.err.println("--- AuthController: Login failed - Bad Credentials for user: " + request.email()); // DEBUG
             // e.printStackTrace(); // Optional: Uncomment for full stack trace
             return ResponseEntity.status(401).body("Invalid email or password");
        } catch (UsernameNotFoundException e) { // Catch if user service throws error OR if user disappears after auth
             System.err.println("--- AuthController: Login failed - Username Not Found or inconsistent state for user: " + request.email() + ". Message: " + e.getMessage()); // DEBUG
             return ResponseEntity.status(401).body("Invalid email or password");
        }
         catch (Exception e) { // Catch any other unexpected exceptions during authentication OR token generation
            System.err.println("--- AuthController: Login failed - Unexpected error after authentication attempt for user: " + request.email()); // DEBUG
            System.err.println("Exception type: " + e.getClass().getName());
            System.err.println("Exception message: " + e.getMessage());
             e.printStackTrace(); // Print full trace for unexpected errors
            return ResponseEntity.status(401).body("An unexpected error occurred during login."); // More specific message
        }
    }
}

