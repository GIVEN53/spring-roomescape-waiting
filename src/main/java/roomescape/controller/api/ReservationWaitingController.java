package roomescape.controller.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.request.ReservationRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.security.Accessor;
import roomescape.security.Auth;
import roomescape.service.ReservationWaitingService;

@RestController
@RequestMapping("/reservations/waiting")
@Validated
public class ReservationWaitingController {

    private final ReservationWaitingService reservationWaitingService;

    public ReservationWaitingController(ReservationWaitingService reservationWaitingService) {
        this.reservationWaitingService = reservationWaitingService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservationWaiting(
            @Valid @RequestBody ReservationRequest request,
            @Auth Accessor accessor) {
        long memberId = accessor.id();
        ReservationResponse response = reservationWaitingService.addReservationWaiting(
                request.toCreateReservationRequest(memberId));
        URI location = URI.create("/reservations/waiting/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservationWaiting(@Positive @PathVariable long id, @Auth Accessor accessor) {
        long memberId = accessor.id();
        reservationWaitingService.deleteReservationWaiting(id, memberId);
    }
}
