package hu.flowacademy.qasitespring.service;

import hu.flowacademy.qasitespring.exception.ValidationException;
import hu.flowacademy.qasitespring.model.Answer;
import hu.flowacademy.qasitespring.model.Question;
import hu.flowacademy.qasitespring.model.User;
import hu.flowacademy.qasitespring.repository.AnswerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AnswerServiceUnitTest {

    private static final String id = UUID.randomUUID().toString();
    public static final String ANSWER = "some answer id";
    private static final User user = User.builder().id(UUID.randomUUID().toString()).username("admin").build();
    public static final LocalDateTime NOW = LocalDateTime.now();
    public static final String UPDATED_ANSWER = "updated answer";

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private AnswerService answerService;

    @Test
    public void testAnswerCreate() {
        var answerId = UUID.randomUUID().toString();
        var answer = givenValidAnswer();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null));
        when(answerRepository.save(any())).thenReturn(
                Answer.builder()
                        .id(answerId)
                        .answer(ANSWER)
                        .createdBy(user)
                        .createdAt(NOW)
                        .answered(Boolean.FALSE)
                        .question(answer.getQuestion())
                        .build()
        );
        Answer result = answerService.save(answer);
        thenCheckResult(answer, result);
    }

    @Test
    public void testAnswerCreateValidation() {
        assertThrows(ValidationException.class, () -> answerService.save(givenAnswerWithNullQuestionId()));
        assertThrows(ValidationException.class, () -> answerService.save(givenAnswerWithEmptyQuestionId()));
        assertThrows(ValidationException.class, () -> answerService.save(givenNullAnswerValue()));
        assertThrows(ValidationException.class, () -> answerService.save(givenEmptyAnswerValue()));
    }

    @Test
    public void testAnswerUpdate() {
        var answer = givenValidUpdateAnswer();
        when(answerRepository.findById(answer.getId()))
                .thenReturn(Optional.of(answer));
        Answer result = answerService.update(answer);
        verify(answerRepository).update(answer.getAnswer(), answer.getId());
        thenCheckResult(answer, result);
    }

    @Test
    public void testAnswerUpdateValidation() {
        assertThrows(ValidationException.class, () -> answerService.update(
                extendWithId(givenNullAnswerValue())
        ));
        assertThrows(ValidationException.class, () -> answerService.update(
                extendWithId(givenEmptyAnswerValue())
        ));
        assertThrows(ValidationException.class, () -> answerService.update(givenNullAnswerValue()));
        assertThrows(ValidationException.class, () -> answerService.update(givenEmptyAnswerValue()));
    }

    private Answer extendWithId(Answer answer) {
        return answer.toBuilder().id(id).build();
    }

    private Answer givenAnswerWithEmptyQuestionId() {
        return Answer.builder().question(Question.builder().id("").build()).build();
    }

    private Answer givenAnswerWithNullQuestionId() {
        return Answer.builder().question(Question.builder().build()).build();
    }

    private Answer givenNullAnswerValue() {
        return Answer.builder().question(
                Question.builder().id(UUID.randomUUID().toString())
                        .build())
                .build();
    }

    private Answer givenEmptyAnswerValue() {
        return Answer.builder().answer("").question(
                Question.builder().id(UUID.randomUUID().toString())
                        .build()
        ).build();
    }

    private Answer givenValidAnswer() {
        return Answer.builder()
                .answer(ANSWER)
                .question(
                        Question.builder()
                                .id(UUID.randomUUID().toString())
                                .build()
                )
                .build();
    }

    private Answer givenValidUpdateAnswer() {
        return Answer.builder()
                .id(UUID.randomUUID().toString())
                .question(
                        Question.builder()
                                .id(UUID.randomUUID().toString())
                                .build()
                )
                .createdBy(user)
                .createdAt(LocalDateTime.now())
                .answer(UPDATED_ANSWER)
                .answered(Boolean.FALSE)
                .build();
    }

    private void thenCheckResult(Answer answer, Answer result) {
        assertNotNull(result.getId());
        assertFalse(result.getAnswered());
        assertNotNull(result.getQuestion());
        assertNotNull(result.getCreatedBy());
        assertNotNull(result.getCreatedAt());
        assertEquals(answer.getAnswer(), result.getAnswer());
        assertEquals(user.getId(), result.getCreatedBy().getId());
        assertEquals(answer.getQuestion().getId(), result.getQuestion().getId());
    }
}