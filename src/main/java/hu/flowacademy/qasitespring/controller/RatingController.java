package hu.flowacademy.qasitespring.controller;

import hu.flowacademy.qasitespring.dto.RatingResponseDTO;
import hu.flowacademy.qasitespring.model.RatingKind;
import hu.flowacademy.qasitespring.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PutMapping("/{ratingKind}/{id}/rate")
    public RatingResponseDTO rate(@PathVariable String ratingKind,
                                  @PathVariable String id) {
        return new RatingResponseDTO(
                ratingService.rate(RatingKind.valueOf(ratingKind), UUID.fromString(id).toString())
        );
    }

    @PutMapping("/{ratingKind}/{id}/unrate")
    public RatingResponseDTO unrate(@PathVariable String ratingKind,
                                  @PathVariable String id) {
        return new RatingResponseDTO(
                ratingService.unrate(RatingKind.valueOf(ratingKind), UUID.fromString(id).toString())
        );
    }

    @PutMapping("/{ratingKind}/{id}/rate/dismiss")
    public RatingResponseDTO dismiss(@PathVariable String ratingKind,
                                  @PathVariable String id) {
        return new RatingResponseDTO(
                ratingService.dismiss(RatingKind.valueOf(ratingKind), UUID.fromString(id).toString())
        );
    }

}
