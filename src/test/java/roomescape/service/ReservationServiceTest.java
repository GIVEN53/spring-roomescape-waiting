package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.reservationtime.ReservationTimeRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.dto.response.PersonalReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.support.fixture.MemberFixture;
import roomescape.support.fixture.ReservationFixture;
import roomescape.support.fixture.ReservationTimeFixture;
import roomescape.support.fixture.ThemeFixture;

class ReservationServiceTest extends BaseServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    private Member member;
    private Theme theme;
    private ReservationTime time;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MemberFixture.ADMIN);
        theme = themeRepository.save(ThemeFixture.THEME);
        time = reservationTimeRepository.save(ReservationTimeFixture.TEN);
    }

    @Test
    @DisplayName("예약들을 조회한다.")
    void getReservations() {
        Reservation reservation = ReservationFixture.create("2024-04-09", member, time, theme);
        reservationRepository.save(reservation);

        List<ReservationResponse> responses = reservationService.getReservationsByConditions(
                member.getId(),
                theme.getId(),
                LocalDate.of(2024, 4, 9),
                LocalDate.of(2024, 4, 9)
        );

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(responses).hasSize(1);
            softly.assertThat(responses.get(0).date()).isEqualTo("2024-04-09");
            softly.assertThat(responses.get(0).member().id()).isEqualTo(member.getId());
            softly.assertThat(responses.get(0).theme().id()).isEqualTo(theme.getId());
            softly.assertThat(responses.get(0).time().id()).isEqualTo(time.getId());
        });
    }

    @Test
    @DisplayName("예약을 추가한다.")
    void addReservation() {
        ReservationResponse response = reservationService.addReservation(
                LocalDate.of(2024, 4, 9),
                time.getId(),
                theme.getId(),
                member.getId()
        );

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.date()).isEqualTo("2024-04-09");
            softly.assertThat(response.member().id()).isEqualTo(member.getId());
            softly.assertThat(response.theme().id()).isEqualTo(theme.getId());
            softly.assertThat(response.time().id()).isEqualTo(time.getId());
        });
    }

    @Test
    @DisplayName("id로 예약을 삭제한다.")
    void deleteReservationById() {
        Reservation savedReservation = reservationRepository.save(ReservationFixture.DEFAULT);

        reservationService.deleteReservationById(savedReservation.getId());

        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).isEmpty();
    }

    @Test
    @DisplayName("나의 예약들을 조회한다.")
    void getReservationsByMemberId() {
        Reservation reservation = ReservationFixture.create("2024-04-09", member, time, theme);
        reservationRepository.save(reservation);

        List<PersonalReservationResponse> responses = reservationService.getReservationsByMemberId(member.getId());

        SoftAssertions.assertSoftly(
                softly -> {
                    softly.assertThat(responses).hasSize(1);
                    softly.assertThat(responses.get(0).date()).isEqualTo("2024-04-09");
                    softly.assertThat(responses.get(0).member().id()).isEqualTo(member.getId());
                    softly.assertThat(responses.get(0).theme().id()).isEqualTo(theme.getId());
                    softly.assertThat(responses.get(0).time().id()).isEqualTo(time.getId());
                    softly.assertThat(responses.get(0).status()).isEqualTo("예약 대기");
                }
        );
    }
}
