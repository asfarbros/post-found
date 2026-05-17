package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional; // Import Optional

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("--- CustomUserDetailsService: Attempting to load user by email: " + email); // DEBUG

        // Find our user from the database
        Optional<User> userOptional = userRepository.findByEmail(email); // Use Optional

        if (userOptional.isEmpty()) 
            {
             System.out.println("--- CustomUserDetailsService: User NOT FOUND with email: " + email); // DEBUG
             throw new UsernameNotFoundException("User not found with email: " + email);
        }

        User user = userOptional.get();
        System.out.println("--- CustomUserDetailsService: User FOUND: " + user.getEmail()); // DEBUG
        System.out.println("--- CustomUserDetailsService: Returning HASHED password: " + user.getPassword()); // DEBUG


        // Convert our User entity into a Spring Security UserDetails object
        // The third argument is for roles/authorities, which we aren't using yet
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(), // Pass the HASHED password from your database User object
                new ArrayList<>() // Empty list for authorities
        );
    }
}
