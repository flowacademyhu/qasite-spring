package hu.flowacademy.qasitespring.service;

import hu.flowacademy.qasitespring.exception.NoContentException;
import hu.flowacademy.qasitespring.exception.ValidationException;
import hu.flowacademy.qasitespring.model.Question;
import hu.flowacademy.qasitespring.model.Status;
import hu.flowacademy.qasitespring.model.User;
import hu.flowacademy.qasitespring.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    /**
     * save setting up the question's missing fields and storing it into the database
     * @param question
     * @return
     */
    public Question save(Question question) {
        validate(question);
        question.setId(UUID.randomUUID().toString());
        question.setStatus(Status.PUBLISHED);
        question.setCreatedAt(LocalDateTime.now());
        question.setCreatedBy((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return questionRepository.save(question);
    }

    /**
     * findAll getting the specified page from the questions table
     * @param limit the SQL's limit value, means the page size
     * @param offset the SQL's offset value, means the page "number"
     * @param sort the SQL's order by value, means the order of the data
     * @return questions page
     */
    public Page<Question> findAll(int limit, int offset, String sort) {
        return questionRepository.findAll(
                PageRequest.of(
                        offset != 0 ? (limit / offset) - 1 : 0,
                        limit,
                        Sort.by(sort)
                )
        );
    }

    /**
     * validate validating the savable question object
     * it has to be title and description
     * title can't be longer then 100 chars
     * description can't be longer then 600 chars
     * the id has to be null
     * @param question
     * @throws ValidationException if something went wrong
     */
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

    public Question findOne(String id) {
        return questionRepository.findById(id).orElseThrow(() -> new NoContentException("id:"+id));
    }

    public void delete(String id) {
        questionRepository.deleteById(id);
    }
}
