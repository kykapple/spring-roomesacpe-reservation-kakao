package nextstep.service;

import nextstep.domain.reservation.Reservation;
import nextstep.domain.theme.Theme;
import nextstep.domain.reservation.repository.ReservationRepository;
import nextstep.dto.request.CreateReservationRequest;
import nextstep.error.ApplicationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ThemeService themeService;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void 예약을_생성한다() {
        // given
        CreateReservationRequest createReservationRequest = new CreateReservationRequest("2023-01-09", "13:00", "eddie-davi", "테마 이름");
        Theme theme = new Theme(createReservationRequest.getThemeName(), "테마 설명", 29_000);
        Reservation reservation = new Reservation(51L, LocalDate.parse(createReservationRequest.getDate()), LocalTime.parse(createReservationRequest.getTime()), createReservationRequest.getName(), theme);

        given(themeService.findThemeByName(createReservationRequest.getThemeName()))
                .willReturn(theme);
        given(reservationRepository.existsByDateAndTime(LocalDate.parse(createReservationRequest.getDate()), LocalTime.parse(createReservationRequest.getTime())))
                .willReturn(false);
        given(reservationRepository.save(any(Reservation.class)))
                .willReturn(reservation);

        // when
        Long reservationId = reservationService.createReservation(createReservationRequest);

        // then
        assertThat(reservationId).isEqualTo(reservation.getId());
    }

    @Test
    void 동일한_날짜와_시간의_예약을_생성할_경우_예외가_발생한다() {
        // given
        CreateReservationRequest createReservationRequest = new CreateReservationRequest("2023-01-09", "13:00", "eddie-davi", "테마 이름");

        given(reservationRepository.existsByDateAndTime(LocalDate.parse(createReservationRequest.getDate()), LocalTime.parse(createReservationRequest.getTime())))
                .willReturn(true);

        // when, then
        assertThatThrownBy(() -> reservationService.createReservation(createReservationRequest))
                .isInstanceOf(ApplicationException.class);
    }

    @Test
    void 존재하지_않는_예약을_조회할_경우_예외가_발생한다() {
        // given
        Long invaildId = 500L;

        given(reservationRepository.findById(invaildId))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> reservationService.findReservationById(invaildId))
                .isInstanceOf(ApplicationException.class);
    }

}
