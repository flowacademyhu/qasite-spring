package hu.flowacademy.qasitespring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
/**
 * ResponseMessageDTO is a Data Transfer Object
 * which only used for communication between server and client
 * but NOT stored in any kind of database(IMPORTANT!!!!)
 */
public class ResponseMessageDTO {
    @JsonProperty("msg")
    private final String message;
}
