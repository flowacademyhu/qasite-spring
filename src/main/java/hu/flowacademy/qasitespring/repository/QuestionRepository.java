package hu.flowacademy.qasitespring.repository;

import hu.flowacademy.qasitespring.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {
}
