package com.cc.cinemaprocessor.service;

import com.cc.cinemaprocessor.repository.FilmRepository;
import com.cc.cinemaprocessor.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
public class SchedulingService {

    private final FilmRepository filmRepository;
    private final ReservationRepository reservationRepository;

    public SchedulingService(FilmRepository filmRepository, ReservationRepository reservationRepository) {
        this.filmRepository = filmRepository;
        this.reservationRepository = reservationRepository;
    }

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void deleteFilmsAndReservations() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1L);
        filmRepository.deleteAllByStartTimeLessThan(yesterday);
        reservationRepository.deleteAllByStartTimeLessThan(yesterday);
        log.info("Deleted films and reservations for previous date");
    }
}