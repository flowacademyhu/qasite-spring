package hu.flowacademy.qasitespring.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.flowacademy.qasitespring.dto.QuestionListResponseDTO;
import hu.flowacademy.qasitespring.dto.QuestionRequestDTO;
import hu.flowacademy.qasitespring.model.Question;
import hu.flowacademy.qasitespring.model.Status;
import hu.flowacademy.qasitespring.model.User;
import hu.flowacademy.qasitespring.repository.QuestionRepository;
import hu.flowacademy.qasitespring.repository.UserRepository;
import lombok.SneakyThrows;
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

import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class QuestionControllerTest {

    private static final String QUESTION_TITLE = "question title";
    private static final String QUESTION_DESCRIPTION = "question description";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @SneakyThrows
    public void whenSendingValidQuestionItStoringIntoDB() {
        var body = mockMvc.perform(
                post("/api/questions")
                        .content(givenQuestionRequest())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        Question result = new ObjectMapper().readValue(body, Question.class);
        assertNotNull(result.getId());
        Question question = questionRepository.findById(result.getId()).get();
        assertNotNull(question);
        assertEquals(QUESTION_TITLE, question.getTitle());
        assertEquals(QUESTION_TITLE, result.getTitle());
        assertEquals(QUESTION_DESCRIPTION, question.getDescription());
        assertEquals(QUESTION_DESCRIPTION, result.getDescription());
        assertEquals(Status.PUBLISHED, question.getStatus());
        assertEquals(Status.PUBLISHED, result.getStatus());
        assertNotNull(question.getCreatedBy());
        assertNotNull(result.getCreatedBy());
        assertNotNull(question.getCreatedAt());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    @SneakyThrows
    public void whenGettingQuestionsWithoutAnyParamTheDefaultWillSet() {
        IntStream.range(0, 11).forEach(i ->
                questionRepository.save(Question.builder().id(Integer.toString(i)).build())
        );
        var body = mockMvc.perform(
                get("/api/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        QuestionListResponseDTO result = new ObjectMapper().readValue(body, QuestionListResponseDTO.class);
        assertEquals(11, result.getCount());
        assertNotNull(result.getData());
        assertEquals(10, result.getData().size());

        IntStream.range(0, 11).forEach(i ->
                questionRepository.deleteById(Integer.toString(i))
        );
    }

    @Test
    @SneakyThrows
    public void whenGettingQuestionsWithParamsSetGotRightResult() {
        IntStream.range(0, 3).forEach(i ->
                questionRepository.save(Question.builder().id(Integer.toString(i)).build())
        );
        var body = mockMvc.perform(
                get("/api/questions")
                        .param("limit", "2")
                        .param("offset", "1")
                        .param("sort", "id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        QuestionListResponseDTO result = new ObjectMapper().readValue(body, QuestionListResponseDTO.class);

        assertEquals(3, result.getCount());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals("2", result.getData().get(0).getId());

        IntStream.range(0, 3).forEach(i ->
                questionRepository.deleteById(Integer.toString(i))
        );
    }

    @SneakyThrows
    private String givenQuestionRequest() {
        return new ObjectMapper().writeValueAsString(new QuestionRequestDTO(QUESTION_TITLE, QUESTION_DESCRIPTION));
    }

    @SneakyThrows
    private String login() {
        var user = userRepository.findByUsername("admin").map(u -> {
            u.setPassword("admin");
            return u;
        }).orElse(signUp());

        String response = mockMvc.perform(post("/login")
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
    public User signUp() {
        mockMvc.perform(
                post("/api/users")
                        .content(new ObjectMapper().writeValueAsBytes(
                                Map.of("username", "admin",
                                        "password", "admin",
                                        "full_name", "admin")))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.msg").value("Successfully registration!"));

        return User.builder().username("admin").password("admin").build();
    }

}