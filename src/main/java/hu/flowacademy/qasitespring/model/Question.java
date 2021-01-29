package hu.flowacademy.qasitespring.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "questions")
public class Question {
    @Id
    private String id;
    private String title;
    @Lob // will change the default type from VARCHAR(255) to TEXT
//    @Column(columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "created_at")
    /**
     * Using @Column, to change table's column name: converting camelCase to snake_case
     */
    @JsonProperty("created_at")
    /**
     * Always use them!!!!!!!!!
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;
    @ManyToOne
    /**
     * Shows that questions table stores an id from the users table
     * */
    @JoinColumn(name = "created_by")
    /**
     * @JoinColumn shows that this column isn't regular and storing foreign keys
     * Using @JoinColumn, to change table's column name: converting camelCase to snake_case
     */
    @JsonProperty("created_by")
    private User createdBy;
}
