package hu.flowacademy.qasitespring.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.flowacademy.qasitespring.exception.BadCredentials;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.Date;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    /**
     * JWT token expires after 30 minutes
     */
    private static final long EXPIRATION_TIME = 1000 * 60 * 30;

    private final String jwtKey;
    private final ObjectMapper objectMapper;

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                String jwtKey,
                                ObjectMapper objectMapper) {
        super(authenticationManager);
        this.jwtKey = jwtKey;
        this.objectMapper = objectMapper;
    }

    /**
     * attemptAuthentication will process the incoming request
     * we'll try to parse the request's body to Java object
     * after that validate the username and password, these shouldn't be empty strings or nulls
     * if the input is valid, we pass the username, password through the Spring's authentication processor
     * which will check the username in the database and compare the passwords
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            AuthData authData = parseRequestBody(request);

            return processAuthentication(authData);
        } catch (IOException e) {
            log.error("Invalid request format", e);
            log.error("Request was {}", request);
            throw new BadCredentials();
        }
    }

    /**
     * After login success, we'll build a JWT token for the newly authenticated user
     * Send the jwt token as response
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        log.info("Building jwt token for user: {}", authResult.getName());
        Date exp = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
        Key key = Keys.hmacShaKeyFor(jwtKey.getBytes());
        Claims claims = Jwts.claims().setSubject(((UserDetails) authResult.getPrincipal()).getUsername());
        String token = Jwts.builder().setClaims(claims).signWith(key, SignatureAlgorithm.HS512).setExpiration(exp).compact();

        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().printf(objectMapper.writeValueAsString(
                Token.builder()
                        .token(token)
                        .revokeToken("")
                        .authKind(AuthKind.DefaultLogin)
                        .build()
        ));
    }

    /**
     * Pass the auth credentials through the Spring authentication processor
     * We'll do the next steps in UserDetailsService
     * @param authData
     * @return
     */
    private Authentication processAuthentication(AuthData authData) {
        return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        authData.getUsername(),
                        authData.getPassword()
                )
        );
    }

    /**
     * Try to do the same as @RequestBody in controller
     * Parse the request's body to AuthData and validate it
     * @param request the input HTTP request
     * @return with parsed AuthData
     * @throws IOException if request body format isn't parsable
     * @throws BadCredentials if validate fails
     */
    private AuthData parseRequestBody(HttpServletRequest request) throws IOException {
        AuthData authData = objectMapper.readValue(request.getReader(), AuthData.class);
        validate(authData);
        return authData;
    }

    /**
     * validate checks username and password for empty string or null
     * @param authData
     * @throws BadCredentials if one of them is invalid
     */
    private void validate(AuthData authData) {
        if (!StringUtils.hasText(authData.getUsername()) ||
                !StringUtils.hasText(authData.getPassword())) {
            throw new BadCredentials();
        }
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class AuthData {
    private String username;
    private String password;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Token {
    private String token;
    @JsonProperty("revoke_token")
    private String revokeToken;
    @JsonProperty("auth_kind")
    private AuthKind authKind;
}

enum AuthKind {
    DefaultLogin
}