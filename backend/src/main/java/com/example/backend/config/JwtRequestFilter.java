package com.example.backend.config;

import com.example.backend.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        // 1. Check for the "Bearer" token in the header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // "Bearer ".length()
            try {
                email = jwtUtil.getEmailFromToken(jwt);
            } catch (ExpiredJwtException e) {
                System.out.println("JWT has expired");
            } catch (Exception e) {
                System.out.println("Error parsing JWT");
            }
        }

        // 2. If we have an email AND the user is not already logged in...
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 3. ...load the user from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

            // 4. ...and validate the token
            if (jwtUtil.validateToken(jwt)) {
                
                // 5. If valid, set up Spring Security authentication
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // This is the line that "logs in" the user for this request
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 6. Pass the request along to the next filter
        filterChain.doFilter(request, response);
    }
}
