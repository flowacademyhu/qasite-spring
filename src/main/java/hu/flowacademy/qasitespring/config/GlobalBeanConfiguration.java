package hu.flowacademy.qasitespring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalBeanConfiguration {

    /**
     * Making component from external class
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
