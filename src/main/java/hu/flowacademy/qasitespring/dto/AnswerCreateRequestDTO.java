package hu.flowacademy.qasitespring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerCreateRequestDTO {
    @JsonProperty("question_id")
    private String questionId;
    private String answer;
}
