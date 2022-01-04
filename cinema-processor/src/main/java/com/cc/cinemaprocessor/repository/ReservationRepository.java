package com.cc.cinemaprocessor.repository;

import com.cc.cinemaprocessor.model.Reservation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    List<Reservation> findAllByUsername(String Username);
    void deleteByIdAndUsername(Long id, String username);
    void deleteAllByCinemaName(String cinemaName);
    void deleteAllByFilmName(String filmName);
    void deleteAllByStartTimeLessThan(LocalDateTime date);
}
