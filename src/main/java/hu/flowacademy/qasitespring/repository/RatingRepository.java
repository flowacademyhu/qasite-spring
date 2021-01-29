package hu.flowacademy.qasitespring.repository;

import hu.flowacademy.qasitespring.model.Rating;
import hu.flowacademy.qasitespring.model.RatingKind;
import hu.flowacademy.qasitespring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, String> {
    @Query("SELECT SUM(r.value) FROM Rating r WHERE r.kind=?1 AND r.recordId=?2")
    Integer calculateRatings(RatingKind kind, String id);

    void deleteByKindAndRecordIdAndRatedBy(RatingKind kind, String recordId, User ratedBy);
}
