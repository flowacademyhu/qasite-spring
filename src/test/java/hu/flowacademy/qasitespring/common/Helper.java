package hu.flowacademy.qasitespring.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.flowacademy.qasitespring.model.User;
import hu.flowacademy.qasitespring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@RequiredArgsConstructor
public class Helper {

    private final MockMvc mockMvc;
    private final UserRepository userRepository;

    @SneakyThrows
    public String login() {
        var user = signUp("admin" + UUID.randomUUID().toString(),
                "admin", "admin");

        String response = mockMvc.perform(post("/api/login")
                .content(new ObjectMapper().writeValueAsBytes(Map.of(
                        "username", user.getUsername(),
                        "password", user.getPassword())))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        var responseData = new ObjectMapper().readValue(response, new TypeReference<Map<String, String>>() {
        });

        return responseData.get("token");
    }

    /**
     * @return
     * @SneakyThrows because we don't want to catch the exception, if we got, the test have to fail
     */
    @SneakyThrows
    public User signUp(String username, String password, String fullName) {
        mockMvc.perform(
                post("/api/users")
                        .content(new ObjectMapper().writeValueAsBytes(
                                Map.of("username", username,
                                        "password", password,
                                        "full_name", fullName)))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.msg").value("Successfully registration!"));

        return userRepository.findByUsername(username).map(
                user -> user.toBuilder().password(password).build()
        ).orElseThrow();
    }

}
