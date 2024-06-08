package com.motherlove.security;

import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.repositories.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    private final TokenRepository tokenRepository;

    public String generateToken(Authentication authentication) {
        UserDetails username = (UserDetails) authentication.getPrincipal();
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + jwtExpirationDate);
        return Jwts.builder()
                .setSubject(username.getUsername())
                .setIssuedAt(currentDate)
                .setExpiration(expirationDate)
                .signWith(key())
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "JWT token is empty or null");
        }else{
            try {
                Jwts.parserBuilder()
                        .setSigningKey(key())
                        .build()
                        .parse(token);
                return true;
            } catch (MalformedJwtException e) {
                throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
            } catch (ExpiredJwtException e) {
                throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Expired JWT token");
            } catch (UnsupportedJwtException e) {
                throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
            } catch (IllegalArgumentException e) {
                throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "JWT claims string is empty");
            }
        }
    }

    public boolean isValid(String token){
        boolean isValidToken =  tokenRepository.findByToken(token).isLoggedOut();
        return isValidToken;
    }
}
