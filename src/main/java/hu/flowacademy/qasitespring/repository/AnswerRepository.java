package hu.flowacademy.qasitespring.repository;

import hu.flowacademy.qasitespring.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, String> {
}
