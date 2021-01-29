package hu.flowacademy.qasitespring.dto;

import hu.flowacademy.qasitespring.model.Question;
import lombok.*;

import java.util.List;

/**
 * QuestionListResponseDTO is a Data Transfer Object (DTO)
 * this will be the response of the GET /api/questions endpoint
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionListResponseDTO {
    /**
     * Storing the counts of all found element (this can be more than data.size())
     */
    private long count;
    /**
     * Storing the questions of the current page
     */
    private List<Question> data;
}
