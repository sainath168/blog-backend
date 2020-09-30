package com.project.blog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.Key;

@Service
public class JwtProvider {

    private Key key;

    @PostConstruct
    void getKey() {
        key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String generateToken(Authentication authentication) {
        User pricipal = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(pricipal.getUsername())
                .signWith(key)
                .compact();
    }

    /**
     * Parse the jwt with the same key that is used to generate actually
     * @param jwt
     * @return
     */
    public boolean validateToken(String jwt) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(jwt);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public String getUsernameFromJwt(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jwt)
                .getBody();
        return claims.getSubject();
    }
}
