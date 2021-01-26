package hu.flowacademy.qasitespring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
/**
 * Importing GlobalBeanConfiguration to be able to inject ObjectMapper
 */
@Import(GlobalBeanConfiguration.class)
@EnableWebSecurity
/**
 * WebSecurityConfigurerAdapter provides us a default security config
 * which will reconfigured by us with method overriding
 *
 * SecurityConfiguration stores all the security related configurations
 * and implements the custom security solutions
 */
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Value("${jwt.key}")
    private String jwtKey;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            /**
             * set Cross-Origin Resource-Sharing disabled, to prevent frontend CORS errors
             * only disable it on security related endpoints, ex. /api/login
             */
            .cors().disable()
            /**
             * disable csrf, which is a deprecated security step for server side rendering apps
             */
            .csrf().disable()
            /**
             * authorizeRequests gives chance for us to customize the endpoint access controls
             * we can accept or deny access on certain endpoint patterns and/or methods
             */
            .authorizeRequests()
                /**
                 * with antMatchers(HttpMethod.POST, "/api/users").permitAll() we will accept
                 * to any clients to send POST requests to /api/users, without any authentication
                 */
                .antMatchers(HttpMethod.POST, "/api/users").permitAll()
                /**
                 * for any other endpoints only available after authentication
                 */
                .anyRequest().authenticated()
        .and()
                .addFilter(new AuthenticationFilter(authenticationManager(), jwtKey, objectMapper))
                .addFilter(new AuthorizationFilter(authenticationManager(), jwtKey, userDetailsService))
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    /**
     * Creates a Spring component for the interface PasswordEncoder
     * with the implementation BCryptPasswordEncoder.
     * So we can inject it everywhere in the app
     * @return an instance of BCryptPasswordEncoder default constructor
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
