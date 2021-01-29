package hu.flowacademy.qasitespring.service;

import hu.flowacademy.qasitespring.model.Rating;
import hu.flowacademy.qasitespring.model.RatingKind;
import hu.flowacademy.qasitespring.model.User;
import hu.flowacademy.qasitespring.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    public static final Integer positiveRating = 1;
    public static final Integer negativeRating = -1;

    public Integer rate(RatingKind kind, String id) {
        // TODO the owner of the record can't rate it
        // TODO user can't rate twice
        ratingRepository.save(
                Rating.builder()
                        .id(UUID.randomUUID().toString())
                        .kind(kind)
                        .recordId(id)
                        .value(positiveRating)
                        .ratedAt(LocalDateTime.now())
                        .ratedBy(getCurrentUser())
                        .build()
        );
        return ratingRepository.calculateRatings(kind, id);
    }

    public Integer unrate(RatingKind kind, String id) {
        // TODO the owner of the record can't rate it
        // TODO user can't unrate twice
        ratingRepository.save(
                Rating.builder()
                        .id(UUID.randomUUID().toString())
                        .kind(kind)
                        .recordId(id)
                        .value(negativeRating)
                        .ratedAt(LocalDateTime.now())
                        .ratedBy(getCurrentUser())
                        .build()
        );
        return ratingRepository.calculateRatings(kind, id);
    }

    public Integer dismiss(RatingKind kind, String id) {
        ratingRepository.deleteByKindAndRecordIdAndRatedBy(kind, id,
                getCurrentUser());
        return ratingRepository.calculateRatings(kind, id);
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
