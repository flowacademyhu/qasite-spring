package hu.flowacademy.qasitespring.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
public class AuthorizationFilter extends BasicAuthenticationFilter {
    private static final String AUTHORIZATION = "Authorization";
    private final String jwtKey;
    private final UserDetailsService userDetailsService;

    public AuthorizationFilter(AuthenticationManager authenticationManager, String jwtKey, UserDetailsService userDetailsService) {
        super(authenticationManager);
        this.jwtKey = jwtKey;
        this.userDetailsService = userDetailsService;
    }


    /**
     * doFilterInternal processing every received request, the following way:
     * Fist checks the request header is containing the Authorization,
     * if not, send through the request to the next filter (which are fully under Spring's control).
     * If there is Authorization header, try to process it, so remove the "Bearer " prefix from the header value
     * and try to validate the signing of the token with the configured signing key (jwtKey),
     * if it works, we'll get the claims, so we can reach the username in "sub" property, then doing the authorization,
     * otherwise the jwt parser throws exception.
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authorization = request.getHeader(AUTHORIZATION);
        if (!StringUtils.hasText(authorization)) {
            /**
             * If header is missing, process as unauthorized claims
             */
            chain.doFilter(request, response);
            return;
        }

        String jwtToken = authorization.replace("Bearer ", "");

        Object claims = Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(jwtKey.getBytes()))
                .parse(jwtToken).getBody();

        if (claims instanceof Map) {
            // the context only stores the authorized username
            String subjectName = ((Map) claims).get("sub").toString();
            doAuthorize(subjectName);
        }

        log.info("JWT parsed data: {}", claims);


        chain.doFilter(request, response);
    }

    /**
     * doAuthorize will check the username, which stored by the jwt token,
     * if there is a user in the database, we get it and put it into the context.
     * The context is a special object which related to the request's session, so if we put the user into it,
     * then we can reach this user object everywhere in the request lifecycle,
     * for example in a secure Controller method or in service.
     * (Mostly we're getting this object in the controller)
     * @param subjectName the username from the jwt token
     */
    private void doAuthorize(String subjectName) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(subjectName);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        userDetails.getAuthorities())
        );
    }
}
