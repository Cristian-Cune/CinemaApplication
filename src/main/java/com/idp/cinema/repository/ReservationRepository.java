package com.idp.cinema.repository;

import com.idp.cinema.model.Reservation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    List<Reservation> findAllByUsername(String Username);
    void deleteByIdAndUsername(Long id, String username);
    void deleteAllByCinemaName(String cinemaName);
}
