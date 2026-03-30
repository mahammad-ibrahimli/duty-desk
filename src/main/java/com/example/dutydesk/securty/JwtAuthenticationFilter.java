package com.example.dutydesk.securty;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        Enumeration<String> headers = request.getHeaders("Authorization");
        String header = null;
        String fallbackBearer = null;
        Pattern jwtPattern = Pattern.compile("([A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+)");
        for (String h : Collections.list(headers)) {
            if (h == null) continue;
            String ht = h.trim();
            if (ht.length() < 8) continue;
            if (ht.regionMatches(true, 0, "Bearer ", 0, 7)) {
                String candidate = ht.substring(7).trim();
                if ((candidate.startsWith("\"") && candidate.endsWith("\"")) || (candidate.startsWith("'") && candidate.endsWith("'"))) {
                    candidate = candidate.substring(1, candidate.length() - 1).trim();
                }
                int dots = 0;
                for (int i = 0; i < candidate.length(); i++) {
                    if (candidate.charAt(i) == '.') dots++;
                }
                if (dots == 2) {
                    header = ht;
                    break;
                }
                Matcher m = jwtPattern.matcher(candidate);
                if (m.find()) {
                    header = "Bearer " + m.group(1);
                    break;
                }
                if (fallbackBearer == null) {
                    fallbackBearer = ht;
                }
            } else if (ht.toLowerCase().contains("bearer")) {
                if (fallbackBearer == null) {
                    fallbackBearer = ht;
                }
            }
        }
        if (header == null) {
            header = fallbackBearer;
        }
        if (header == null) {
            logger.warn("Authorization header is missing or does not contain Bearer");
            filterChain.doFilter(request, response);
            return;
        }

        boolean isBearer = header.regionMatches(true, 0, "Bearer ", 0, 7);
        if (!isBearer) {
            logger.warn("Authorization header does not start with Bearer (case-insensitive check)");
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = header.substring(7).trim();
        if ((jwtToken.startsWith("\"") && jwtToken.endsWith("\"")) || (jwtToken.startsWith("'") && jwtToken.endsWith("'"))) {
            jwtToken = jwtToken.substring(1, jwtToken.length() - 1).trim();
        }
        int dotCount = 0;
        for (int i = 0; i < jwtToken.length(); i++) {
            if (jwtToken.charAt(i) == '.') dotCount++;
        }
        if (dotCount != 2) {
            Matcher matcher = jwtPattern.matcher(jwtToken);
            if (matcher.find()) {
                jwtToken = matcher.group(1);
                logger.info("Extracted JWT from non-standard Authorization header");
            } else {
                logger.warn("JWT token format is invalid: expected 2 dots, found {}", dotCount);
                filterChain.doFilter(request, response);
                return;
            }
        }
        String userEmail;

        try {
            userEmail = jwtService.extractUsername(jwtToken);
            logger.info("Extracted username from token: {}", userEmail);
        } catch (Exception exception) {
            logger.error("Failed to extract username from token: {}", exception.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(userEmail);
            } catch (Exception ex) {
                logger.warn("User loading failed for {}: {}", userEmail, ex.getMessage());
                filterChain.doFilter(request, response);
                return;
            }
            if (jwtService.isTokenValid(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                logger.info("Authentication set for user: {}", userEmail);
            } else {
                logger.warn("Token is not valid for user: {}", userEmail);
            }
        }

        filterChain.doFilter(request, response);
    }
}
