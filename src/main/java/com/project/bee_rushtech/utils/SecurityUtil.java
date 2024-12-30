package com.project.bee_rushtech.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.util.Base64;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.responses.LoginResponse;
import com.project.bee_rushtech.responses.LoginResponse.UserLogin;
import com.project.bee_rushtech.services.GoogleService;
import com.project.bee_rushtech.utils.errors.InvalidException;

@Service
public class SecurityUtil {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
    private final JwtEncoder jwtEncoder;
    private final GoogleService googleService;

    public SecurityUtil(JwtEncoder jwtEncoder, GoogleService googleService) {
        this.jwtEncoder = jwtEncoder;
        this.googleService = googleService;
    }

    @Value("${project.jwt.base64-secret}")
    private String jwtKey;

    @Value("${project.jwt.access-token-validity-in-seconds}")
    private long jwtAccessExpiration;

    public String createAccessToken(String email, LoginResponse.UserLogin resLoginDTO) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.jwtAccessExpiration, ChronoUnit.SECONDS);
        String authorities = resLoginDTO.getRole();
        // @formatter:off 
        JwtClaimsSet claims = JwtClaimsSet.builder() 
            .issuedAt(now) 
            .expiresAt(validity) 
            .subject(email) 
            .claim("user", resLoginDTO) 
            .claim("jti", resLoginDTO.getId())
            .claim("permissions", authorities)
            .build(); 
 
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build(); 
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,claims)).getTokenValue();
    }
    @Value("${project.jwt.refresh-token-validity-in-seconds}")
    private long jwtRefreshExpiration;

    public String createRefreshToken(String email, LoginResponse resLoginDTO) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.jwtRefreshExpiration, ChronoUnit.SECONDS);

        // @formatter:off 
        String authorities = resLoginDTO.getUser().getRole();
        JwtClaimsSet claims = JwtClaimsSet.builder() 
            .issuedAt(now) 
            .expiresAt(validity) 
            .subject(email)
            .claim("user", resLoginDTO.getUser()) 
            .claim("permissions", authorities)
            .build(); 
 
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build(); 
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,claims)).getTokenValue();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }
    public Jwt checkValidRefreshToken(String refreshToken) throws InvalidException{
       NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
                try {
                    return jwtDecoder.decode(refreshToken);
                } catch (Exception e) {
                    throw new InvalidException("Invalid Token");
                }
    }



    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user.
     */
    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
            .filter(authentication -> authentication.getCredentials() instanceof String)
            .map(authentication -> (String) authentication.getCredentials());
    }

    public UserLogin getUserFromToken(String token) throws InvalidException {
        Jwt tokenDecoded = this.checkValidRefreshToken(token);
        Map<String, Object> userMap = (Map<String, Object>) tokenDecoded.getClaims().get("user");
        UserLogin user = new UserLogin();
        user.setId((Long) userMap.get("id"));
        user.setEmail((String) userMap.get("email"));
        user.setName((String) userMap.get("fullName"));
        user.setRole((String) userMap.get("role"));
        return user;
    }


}