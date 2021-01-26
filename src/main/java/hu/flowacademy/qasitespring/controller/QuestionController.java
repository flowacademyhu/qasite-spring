package hu.flowacademy.qasitespring.controller;

import hu.flowacademy.qasitespring.dto.QuestionRequestDTO;
import hu.flowacademy.qasitespring.model.Question;
import hu.flowacademy.qasitespring.service.QuestionService;
import hu.flowacademy.qasitespring.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestionController {

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

}
