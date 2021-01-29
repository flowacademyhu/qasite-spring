package hu.flowacademy.qasitespring.controller;

import hu.flowacademy.qasitespring.model.User;
import hu.flowacademy.qasitespring.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testNegativeSignUpWithoutUsername() throws Exception {
        mockMvc.perform(
                post("/api/users")
                        .content(givenSignUpBodyWithoutUsername())
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPositiveSignUp() throws Exception {
        mockMvc.perform(
                post("/api/users")
                        .content(givenSignUpBody())
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.msg").value("Successfully registration!"));

        thenTestIfUserCreated();
    }

    private void thenTestIfUserCreated() {
        Optional<User> user = userRepository.findByUsername("user");
        assertTrue(user.isPresent());
        assertEquals("user", user.get().getUsername());
        assertNotEquals("password", user.get().getPassword());
        assertEquals("John Testington", user.get().getFullName());
    }

    private String givenSignUpBody() {
        return "{\"username\":\"user\",\"password\":\"password\",\"full_name\":\"John Testington\"}";
    }

    private String givenSignUpBodyWithoutUsername() {
        return "{\"password\":\"password\",\"full_name\":\"John Testington\"}";
    }

}