package hu.flowacademy.qasitespring.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private User user;
}
