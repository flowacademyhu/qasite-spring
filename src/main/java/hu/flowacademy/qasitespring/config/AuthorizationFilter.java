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

    private void doAuthorize(String subjectName) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(subjectName);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        userDetails.getAuthorities())
        );
    }
}
