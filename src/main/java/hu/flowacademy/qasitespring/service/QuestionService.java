package hu.flowacademy.qasitespring.service;

import hu.flowacademy.qasitespring.exception.ValidationException;
import hu.flowacademy.qasitespring.model.Question;
import hu.flowacademy.qasitespring.model.Status;
import hu.flowacademy.qasitespring.model.User;
import hu.flowacademy.qasitespring.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class QuestionService {

    private static final int TITLE_MAX_LENGTH = 100;
    private static final int DESCRIPTION_MAX_LENGTH = 600;

    private final QuestionRepository questionRepository;

    public Question save(Question question) {
        validate(question);
        question.setId(UUID.randomUUID().toString());
        question.setStatus(Status.PUBLISHED);
        question.setCreatedAt(LocalDateTime.now());
        question.setCreatedBy((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return questionRepository.save(question);
    }

    private void validate(Question question) {
        if (StringUtils.hasText(question.getId())) {
            throw new ValidationException("question id");
        }
        if (!StringUtils.hasText(question.getTitle())) {
            throw new ValidationException("question title");
        } else if (question.getTitle().length() > TITLE_MAX_LENGTH) {
            throw new ValidationException("question title length > 100");
        }
        if (!StringUtils.hasText(question.getDescription())) {
            throw new ValidationException("question description");
        } else if (question.getDescription().length() > DESCRIPTION_MAX_LENGTH) {
            throw new ValidationException("question description length > 600");
        }
    }
}
