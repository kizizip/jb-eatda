package com.jbeatda.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final RedisTemplate<String, String> redisTemplate;
    private static final Logger logger = Logger.getLogger(JwtProvider.class.getName());
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String ACCESS_TOKEN_BLACKLIST_PREFIX = "access_token_blacklist:";

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    @Value("${jwt.secret:default_secret_key_for_initial_setup}")
    private String secret;

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractEmail(String token) {
        try {
            return extractClaim(token, claims -> claims.get("email", String.class));
        } catch (Exception e) {
            logger.severe("Failed to extract email: " + e.getMessage());
            throw e;
        }
    }

    public Integer extractUserId(String token) {
        try {
            return Integer.parseInt(extractClaim(token, claims -> claims.get("userId", String.class)));
        } catch (Exception e) {
            logger.severe("Failed to extract userId: " + e.getMessage());
            throw e;
        }
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            // 공백 제거
            token = token.replaceAll("\\s+", "");

            // 기본 형식 검증
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new MalformedJwtException("Invalid JWT token format");
            }

            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            logger.severe("Invalid JWT signature: " + e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.severe("Invalid JWT token: " + e.getMessage());
            throw e;
        } catch (ExpiredJwtException e) {
            logger.info("JWT token is expired: " + e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.severe("JWT token is unsupported: " + e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.severe("JWT claims string is empty: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Error parsing JWT: " + e.getMessage());
            throw e;
        }
    }

    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public String generateToken(String email, Integer userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("userId", String.valueOf(userId));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String email, Integer userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("userId", String.valueOf(userId));

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        // Redis에 refresh token 저장
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + userId,
                refreshToken,
                refreshExpiration,
                TimeUnit.MILLISECONDS
        );

        return refreshToken;
    }

    public boolean validateToken(String token, String email, Integer userId) {
        try {
            if (token == null || token.isEmpty()) {
                return false;
            }

            // 블랙리스트 체크 추가
            if (isAccessTokenBlacklisted(token)) {
                return false;
            }

            final String extractedEmail = extractEmail(token);
            final Integer extractedUserId = extractUserId(token);
            return (extractedEmail.equals(email) &&
                    extractedUserId.equals(userId) &&
                    !isTokenExpired(token));
        } catch (Exception e) {
            logger.warning("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    public Boolean validateRefreshToken(String refreshToken, UserDetails userDetails, String email, Integer userId) {
        try {
            if (refreshToken == null || refreshToken.isEmpty()) {
                return false;
            }

            final String tokenEmail = extractEmail(refreshToken);
            final Integer tokenUserId = extractUserId(refreshToken);
            return (tokenEmail.equals(email) &&
                    tokenUserId.equals(userId) &&
                    !isTokenExpired(refreshToken) &&
                    validateRefreshTokenInRedis(userId, refreshToken));
        } catch (Exception e) {
            logger.warning("Refresh token validation failed: " + e.getMessage());
            return false;
        }
    }

    private Boolean validateRefreshTokenInRedis(Integer userId, String refreshToken) {
        String storedToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);
        return storedToken != null && storedToken.equals(refreshToken);
    }

    public void deleteRefreshToken(Integer userId) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
    }

    public void addToAccessTokenBlacklist(String accessToken) {
        try {
            Date expiration = extractExpiration(accessToken);
            long ttl = expiration.getTime() - System.currentTimeMillis();

            if (ttl > 0) {
                redisTemplate.opsForValue().set(
                        ACCESS_TOKEN_BLACKLIST_PREFIX + accessToken,
                        "blacklisted",
                        ttl,
                        TimeUnit.MILLISECONDS
                );
            }
        } catch (Exception e) {
            logger.severe("Failed to add access token to blacklist: " + e.getMessage());
        }
    }

    public boolean isAccessTokenBlacklisted(String accessToken) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(ACCESS_TOKEN_BLACKLIST_PREFIX + accessToken));
        } catch (Exception e) {
            logger.severe("Failed to check access token blacklist: " + e.getMessage());
            return false;
        }
    }

    public String getAccessTokenFromRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}