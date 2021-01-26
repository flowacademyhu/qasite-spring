package hu.flowacademy.qasitespring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
/**
 * WebSecurityConfigurerAdapter provides us a default security config
 * which will reconfigured by us with method overriding
 *
 * SecurityConfiguration stores all the security related configurations
 * and implements the custom security solutions
 */
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
        // TODO override it when we implement login config
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
                .anyRequest().authenticated();
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
