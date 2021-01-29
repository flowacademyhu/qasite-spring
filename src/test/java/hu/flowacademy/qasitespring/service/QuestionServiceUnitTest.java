package hu.flowacademy.qasitespring.service;

import hu.flowacademy.qasitespring.exception.ValidationException;
import hu.flowacademy.qasitespring.model.Question;
import hu.flowacademy.qasitespring.model.Status;
import hu.flowacademy.qasitespring.model.User;
import hu.flowacademy.qasitespring.repository.QuestionRepository;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionServiceUnitTest {

    private static final String QUESTION_TITLE = "What is this project?";
    private static final String QUESTION_DESCRIPTION = "I'm not sure about what we are doing here...";
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final UUID questionId = UUID.randomUUID();
    private static final UUID userId = UUID.randomUUID();
    private static final User user = User.builder().id(userId.toString()).username("admin").build();
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final String SORT_BY_CREATED_AT = "createdAt";
    private static final PageRequest pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE, Sort.by(SORT_BY_CREATED_AT));

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionService questionService;

    @Test
    public void whenQuestionSavingFieldsWillSet() {
        when(questionRepository.save(any())).thenReturn(Question
                .builder()
                .id(questionId.toString())
                .title(QUESTION_TITLE)
                .description(QUESTION_DESCRIPTION)
                .status(Status.PUBLISHED)
                .createdAt(NOW)
                .createdBy(user)
                .build());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null));

        Question result = questionService.save(Question.builder().title(QUESTION_TITLE).description(QUESTION_DESCRIPTION).build());

        assertNotNull(result.getId());
        assertEquals(questionId.toString(), result.getId());
        assertEquals(QUESTION_TITLE, result.getTitle());
        assertEquals(QUESTION_DESCRIPTION, result.getDescription());
        assertEquals(Status.PUBLISHED, result.getStatus());
        assertEquals(NOW, result.getCreatedAt());
        assertNotNull(result.getCreatedBy());
        assertEquals(userId.toString(), result.getCreatedBy().getId());
    }

    @Test
    public void whenInvalidQuestionSavingValidateThrowsException() {
        assertThrows(ValidationException.class, () -> questionService.save(givenQuestionWithId()));
        assertThrows(ValidationException.class, () -> questionService.save(givenQuestionWithoutTitle()));
        assertThrows(ValidationException.class, () -> questionService.save(givenQuestionWithTitleWithoutDescription()));
        assertThrows(ValidationException.class, () -> questionService.save(givenQuestionWithTooLongTitle()));
        assertThrows(ValidationException.class, () -> questionService.save(givenQuestionWithTooLongDescription()));
    }

    @Test
    public void whenFindAllCalledResultWontBeMoreThanLimitButTotalElementCountCan() {
        when(questionRepository.findAll(pageable)).thenReturn(new PageImpl<>(
                IntStream.range(0, 10)
                        .mapToObj(i -> Question.builder().id(Integer.toString(i)).build())
                        .collect(Collectors.toList()), pageable, 11L));

        Page<Question> results = questionService.findAll(PAGE_SIZE, PAGE_NUMBER, SORT_BY_CREATED_AT);

        assertEquals(10, results.getContent().size());
        assertEquals(11L, results.getTotalElements());
    }

    private Question givenQuestionWithId() {
        return Question.builder().id(questionId.toString()).build();
    }

    private Question givenQuestionWithoutTitle() {
        return Question.builder().description(QUESTION_DESCRIPTION).build();
    }

    private Question givenQuestionWithTitleWithoutDescription() {
        return Question.builder().title(QUESTION_TITLE).build();
    }

    private Question givenQuestionWithTooLongTitle() {
        return Question.builder().title(RandomString.make(200)).build();
    }

    private Question givenQuestionWithTooLongDescription() {
        return Question.builder().title(QUESTION_TITLE).description(RandomString.make(700)).build();
    }
}