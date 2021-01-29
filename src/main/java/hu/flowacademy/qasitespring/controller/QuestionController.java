package hu.flowacademy.qasitespring.controller;

import hu.flowacademy.qasitespring.dto.QuestionListResponseDTO;
import hu.flowacademy.qasitespring.dto.QuestionRequestDTO;
import hu.flowacademy.qasitespring.model.Question;
import hu.flowacademy.qasitespring.service.QuestionService;
import hu.flowacademy.qasitespring.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestionController {

    public static final int DEFAULT_LIMIT = 10;
    public static final int DEFAULT_OFFSET = 0;
    public static final String DEFAULT_SORT = "createdAt";
    private final QuestionService questionService;

    @PostMapping("/questions")
    public Question create(@RequestBody QuestionRequestDTO request) {
        return questionService.save(
                Question.builder()
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .build()
        );
    }

    @GetMapping("/questions")
    public QuestionListResponseDTO findAll(@RequestParam(required = false) Integer limit,
                                           @RequestParam(required = false) Integer offset,
                                           @RequestParam(required = false) String sort) {
        Page<Question> questionPage = questionService.findAll(
                limit == null ? DEFAULT_LIMIT : limit,
                offset == null ? DEFAULT_OFFSET : offset,
                sort == null ? DEFAULT_SORT : sort);
        return QuestionListResponseDTO.builder()
                .count(questionPage.getTotalElements())
                .data(questionPage.getContent())
                .build();
    }
}
