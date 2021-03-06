package hu.flowacademy.qasitespring.controller;

import hu.flowacademy.qasitespring.dto.AnswerCreateRequestDTO;
import hu.flowacademy.qasitespring.dto.AnswerEditRequestDTO;
import hu.flowacademy.qasitespring.model.Answer;
import hu.flowacademy.qasitespring.model.Question;
import hu.flowacademy.qasitespring.service.AnswerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("/answers")
    public Answer create(@RequestBody AnswerCreateRequestDTO requestDTO) {
        return answerService.save(Answer.builder()
                .question(Question.builder()
                        .id(UUID.fromString(requestDTO.getQuestionId()).toString())
                        .build())
                .answer(requestDTO.getAnswer())
                .build());
    }

    @PutMapping("/answers/{id}")
    public Answer update(@PathVariable String id, @RequestBody AnswerEditRequestDTO requestDTO) {
        return answerService.update(
                Answer.builder().id(id).answer(requestDTO.getAnswer()).build()
        );
    }

    @GetMapping("/questions/{questionId}/answers")
    public List<Answer> getQuestionAnswers(@PathVariable String questionId) {
        return answerService.getQuestionAnswers(UUID.fromString(questionId).toString());
    }

}
