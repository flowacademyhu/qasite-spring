package hu.flowacademy.qasitespring.repository;

import hu.flowacademy.qasitespring.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, String> {

    /**
     * @Modifying using when we want to do update or insert operation on database
     * @param answer
     * @param answerId
     * @return
     */
    @Modifying
    @Query("UPDATE Answer a SET a.answer=?1 WHERE a.id=?2")
    void update(String answer, String answerId);

}
