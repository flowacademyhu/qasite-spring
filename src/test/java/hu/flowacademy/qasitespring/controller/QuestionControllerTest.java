package hu.flowacademy.qasitespring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.flowacademy.qasitespring.common.Helper;
import hu.flowacademy.qasitespring.dto.QuestionListResponseDTO;
import hu.flowacademy.qasitespring.dto.QuestionRequestDTO;
import hu.flowacademy.qasitespring.model.Question;
import hu.flowacademy.qasitespring.model.Status;
import hu.flowacademy.qasitespring.repository.QuestionRepository;
import hu.flowacademy.qasitespring.repository.UserRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class QuestionControllerTest {

    private static final UUID QUESTION_ID = UUID.randomUUID();
    public static final String QUESTION_TITLE = "question title";
    public static final String QUESTION_DESCRIPTION = "question description";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserRepository userRepository;
    private Helper helper;

    @BeforeEach
    public void before() {
        helper = new Helper(mockMvc, userRepository);
    }

    @Test
    @SneakyThrows
    public void whenSendingValidQuestionItStoringIntoDB() {
        var body = mockMvc.perform(
                post("/api/questions")
                        .content(givenQuestionRequest())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", helper.login())
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
                        .header("Authorization", helper.login())
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
                        .header("Authorization", helper.login())
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

    @Test
    @SneakyThrows
    public void whenGettingQuestionByIdThenReturn() {
        Question givenQuestion = Question.builder()
                .id(QUESTION_ID.toString())
                .title(QUESTION_TITLE)
                .description(QUESTION_DESCRIPTION).build();
        questionRepository.save(givenQuestion);

        var body = mockMvc.perform(
                get("/api/questions/" + QUESTION_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", helper.login())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        Question result = new ObjectMapper().readValue(body, Question.class);
        assertEquals(givenQuestion.getId(), result.getId());
        assertEquals(givenQuestion.getTitle(), result.getTitle());
        assertEquals(givenQuestion.getDescription(), result.getDescription());

        questionRepository.deleteById(QUESTION_ID.toString());
    }

    @Test
    @SneakyThrows
    public void whenDeletingQuestionByIdThenReturnOk() {
        UUID id = UUID.randomUUID();
        Question givenQuestion = Question.builder()
                .id(id.toString())
                .title(QUESTION_TITLE)
                .description(QUESTION_DESCRIPTION).build();
        questionRepository.save(givenQuestion);

        mockMvc.perform(
                delete("/api/questions/" + id.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", helper.login())
        )
                .andExpect(status().isOk())
        .andReturn();

        thenQuestionIdNotExist(id);
    }

    private void thenQuestionIdNotExist(UUID id) {
        assertTrue(questionRepository.findById(id.toString()).isEmpty());
    }


    @SneakyThrows
    private String givenQuestionRequest() {
        return new ObjectMapper().writeValueAsString(new QuestionRequestDTO(QUESTION_TITLE, QUESTION_DESCRIPTION));
    }

}