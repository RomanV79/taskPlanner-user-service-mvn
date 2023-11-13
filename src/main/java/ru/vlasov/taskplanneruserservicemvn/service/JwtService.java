package ru.vlasov.taskplanneruserservicemvn.service;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.vlasov.taskplanneruserservicemvn.config.security.AppUserDetails;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.token.secret}")
    private String jwtSecret;

    @Value("${jwt.token.lifetime}")
    private Duration jwtLifetime;

    public String generateToken(AppUserDetails appUserDetails) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("email", appUserDetails.getUsername());
        claims.put("name", appUserDetails.getAppUser().getName());
        claims.put("confirmed", appUserDetails.getAppUser().isConfirmed());
        claims.put("roles", getRolesName(appUserDetails));

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + jwtLifetime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(appUserDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims getAllClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (SignatureException sEx) {
            log.error("Invalid signature", sEx);
        } catch (Exception e) {
            log.error("invalid token", e);
        }

        return claims;
    }

    public String getUsername(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public List<String> getRoles(String token) {
        @SuppressWarnings("unchecked")
        List<String> roles = getAllClaimsFromToken(token).get("roles", List.class);
        return roles;
    }

    public boolean isTokenValid(String token, AppUserDetails appUserDetails) {

        try {
            String username = getUsername(token);
            if (username.equals(appUserDetails.getUsername())) {
                return true;
            }
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Jwt not valid -> {}", e.getMessage());
        }
        return false;
    }

    private List<String> getRolesName(UserDetails userDetails) {
        return userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }
}
