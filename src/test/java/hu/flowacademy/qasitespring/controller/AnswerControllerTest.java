package hu.flowacademy.qasitespring.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.flowacademy.qasitespring.common.Helper;
import hu.flowacademy.qasitespring.dto.AnswerCreateRequestDTO;
import hu.flowacademy.qasitespring.dto.AnswerEditRequestDTO;
import hu.flowacademy.qasitespring.model.Answer;
import hu.flowacademy.qasitespring.model.Question;
import hu.flowacademy.qasitespring.model.Status;
import hu.flowacademy.qasitespring.repository.AnswerRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static hu.flowacademy.qasitespring.controller.QuestionControllerTest.QUESTION_DESCRIPTION;
import static hu.flowacademy.qasitespring.controller.QuestionControllerTest.QUESTION_TITLE;
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
public class AnswerControllerTest {

    public static final String ANSWER = "answer is ...";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AnswerRepository answerRepository;
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
    public void testAnswerCreation() {
        Question question = givenSavedQuestion();

        Answer result = new ObjectMapper().readValue(
                whenSuccessfullyCreateAnAnswer(question), Answer.class);
        thenTheResultWillMatchAndValid(question, result);
    }

    @Test
    @SneakyThrows
    public void testUpdateAnswer() {
        Question question = givenSavedQuestion();
        var createdAnswer = new ObjectMapper().readValue(
                whenSuccessfullyCreateAnAnswer(question), Answer.class);
        var updatedAnswer = new ObjectMapper().readValue(
                whenSuccessfullyUpdateAnAnswer(createdAnswer.getId()),
                Answer.class);
        thenTheResultWillMatchAndValid(question, updatedAnswer);
    }

    @Test
    @SneakyThrows
    public void testGetAnswersByQuestionId() {
        Question question = givenSavedQuestion();
        IntStream.range(0, 3).forEach(value -> whenSuccessfullyCreateAnAnswer(question));
        var result = new ObjectMapper().readValue(
                whenGettingAnswersByQuestionId(question.getId()),
                new TypeReference<List<Answer>>() {
                }
        );
        assertNotNull(result);
        assertEquals(3, result.size());
        result.forEach(answer -> {
            assertNotNull(answer.getQuestion());
            assertEquals(question.getId(), answer.getQuestion().getId());
        });
    }

    @SneakyThrows
    private String givenAnswerCreateRequest(String questionId) {
        return new ObjectMapper().writeValueAsString(
                new AnswerCreateRequestDTO(questionId, ANSWER)
        );
    }

    private Question givenSavedQuestion() {
        return questionRepository.save(
                Question.builder()
                        .id(UUID.randomUUID().toString())
                        .title(QUESTION_TITLE)
                        .description(QUESTION_DESCRIPTION)
                        .createdBy(helper.signUp(
                                "answer_user",
                                "answer_user",
                                "answer_user"))
                        .createdAt(LocalDateTime.now())
                        .status(Status.PUBLISHED)
                        .build()
        );
    }

    @SneakyThrows
    private String givenAnswerUpdateRequest() {
        return new ObjectMapper().writeValueAsString(new AnswerEditRequestDTO("edited answer"));
    }

    @SneakyThrows
    private String whenSuccessfullyCreateAnAnswer(Question question) {
        return mockMvc.perform(
                post("/api/answers")
                        .content(givenAnswerCreateRequest(question.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", helper.login())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }

    @SneakyThrows
    private String whenSuccessfullyUpdateAnAnswer(String answerId) {
        return mockMvc.perform(
                put("/api/answers/" + answerId)
                        .content(givenAnswerUpdateRequest())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", helper.login())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }

    @SneakyThrows
    private String whenGettingAnswersByQuestionId(String questionId) {
        return mockMvc.perform(
                get("/api/questions/" + questionId + "/answers")
                        .content(givenAnswerUpdateRequest())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", helper.login())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }


    private void thenTheResultWillMatchAndValid(Question question, Answer result) {
        assertEquals(ANSWER, result.getAnswer());
        assertNotNull(result.getQuestion());
        assertEquals(question.getId(), result.getQuestion().getId());
        assertNotNull(question.getCreatedBy());
        assertNotNull(question.getCreatedAt());
    }

}
