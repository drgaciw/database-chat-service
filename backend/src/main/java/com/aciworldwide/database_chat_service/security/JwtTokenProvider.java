package com.aciworldwide.database_chat_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Provider for generating and validating JWT tokens.
 */
@Component
public class JwtTokenProvider {

    private final String jwtSecret;
    private final long jwtExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration}") long jwtExpiration) {
        this.jwtSecret = jwtSecret;
        this.jwtExpiration = jwtExpiration;
    }

    /**
     * Generate a JWT token for the given authentication.
     *
     * @param authentication the authentication object
     * @return the JWT token
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", authorities)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
    }

    /**
     * Get the username from the JWT token.
     *
     * @param token the JWT token
     * @return the username
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Get the authorities from the JWT token.
     *
     * @param token the JWT token
     * @return the authorities
     */
    public Collection<? extends GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        String roles = claims.get("roles", String.class);

        return Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Create an authentication object from the JWT token.
     *
     * @param token the JWT token
     * @return the authentication object
     */
    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        Collection<? extends GrantedAuthority> authorities = getAuthoritiesFromToken(token);

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    /**
     * Validate the JWT token.
     *
     * @param token the JWT token
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}