package hu.flowacademy.qasitespring.service;

import hu.flowacademy.qasitespring.exception.NoContentException;
import hu.flowacademy.qasitespring.exception.ValidationException;
import hu.flowacademy.qasitespring.model.Answer;
import hu.flowacademy.qasitespring.model.Question;
import hu.flowacademy.qasitespring.model.User;
import hu.flowacademy.qasitespring.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Answer save(Answer answer) {
        validate(answer);
        answer.setId(UUID.randomUUID().toString());
        answer.setAnswered(Boolean.FALSE);
        answer.setCreatedAt(LocalDateTime.now());
        answer.setCreatedBy((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return answerRepository.save(answer);
    }

    private void validate(Answer answer) {
        if (Optional.ofNullable(answer.getQuestion())
                .map(Question::getId)
                .flatMap(id -> StringUtils.hasText(id) ? Optional.of(id) : Optional.empty())
                .isEmpty()) {
            throw new ValidationException("answer question id");
        }
        if (!StringUtils.hasText(answer.getAnswer())) {
            throw new ValidationException("answer answer");
        }
    }

    public Answer update(Answer answer) {
        validateUpdate(answer);
        answerRepository.update(answer.getAnswer(), answer.getId());
        return answerRepository.findById(answer.getId())
                .orElseThrow(() -> new NoContentException("answer id:" + answer.getId()));
    }

    private void validateUpdate(Answer answer) {
        if (!StringUtils.hasText(answer.getId())) {
            throw new ValidationException("edit answer id");
        }
        if (!StringUtils.hasText(answer.getAnswer())) {
            throw new ValidationException("edit answer answer");
        }
    }
}
